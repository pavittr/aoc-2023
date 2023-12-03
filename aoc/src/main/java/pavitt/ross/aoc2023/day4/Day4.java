package pavitt.ross.aoc2023.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day4 {
    public String part1(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return "" + stream.map(line -> toCard(line)).mapToLong(Card::pointValue).sum();
        }
    }

    public String part2(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<Card> cards = stream.map(line -> toCard(line)).toList();
            Map<Long, CardMatches> idToValueMap = cards
                    .stream()
                    .collect(Collectors.toMap(Card::id, Card::matches));

            return "" + cards.reversed().stream()
                    .map(card -> idToValueMap.get(card.id))
                    .mapToLong(matches -> matches.totalValue(idToValueMap))
                    .sum();
        }
    }

    Card toCard(String line) {
        String[] splitByPipe = line.split("\\|");
        String[] idAndWinners = splitByPipe[0].split(": ");
        int id = Integer.parseInt(idAndWinners[0].substring(5).trim());
        List<Integer> winners = Arrays.stream(idAndWinners[1].trim().split(" +"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<Integer> draws = Arrays.stream(splitByPipe[1].trim().split(" +"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return new Card(id, winners, draws);
    }

    record CardMatches(long id, List<Long> matches) {
        Long totalValue(Map<Long, CardMatches> matchMap) {
            if (matches.size() == 0) {
                return 1L;
            }

            return matches
                    .stream() // [5]
                    .map(matchId -> matchMap.get(matchId)) // {5, <>}
                    .mapToLong(cardMatch -> cardMatch.totalValue(matchMap))
                    .sum() + 1;
        }
    }

    record Card(long id, List<Integer> winners, List<Integer> draws) {
        long pointValue() {
            long matches = draws.stream().filter(draw -> winners.contains(draw)).count();
            return 1L << (matches - 1);
        }

        CardMatches matches() {
            long matches = draws.stream().filter(draw -> winners.contains(draw)).count();
            return new CardMatches(id, LongStream
                    .range((long) id + 1, id + matches + 1)
                    .mapToObj(Long::valueOf)
                    .toList());
        }
    }
}
