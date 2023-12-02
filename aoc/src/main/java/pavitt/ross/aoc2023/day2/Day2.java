package pavitt.ross.aoc2023.day2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.IOException;

public class Day2 {
    public String part1(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return ""+stream
            .map(line -> toGame(line))
            .filter(game -> game.isPossible(12,14,13))
            .mapToInt(Game::id)
            .sum();
        }
    }

    public String part2(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
             return ""+stream
             .map(line -> toGame(line))
             .mapToInt(Game::minimumPossible)
             .sum();
        }
    }

    public Pick toPick(String line) {
        int red, blue, green ;
        red = green = blue = 0;
        List<String> elements = Arrays.asList(line.trim().split(" "));

        for (int i = 0; i < elements.size() / 2 ; i++) {
            int amount = Integer.parseInt(elements.get(i*2));
            String color = elements.get(i*2 + 1).replaceAll(",", "");
            switch (color) {
                case "red":
                    red = amount;
                    break;
                case "green":
                    green = amount;
                    break;
                case "blue":
                    blue = amount;
                    break;
                default:
                    throw new IllegalStateException("failed to parse " + color);
            }
        }
        return new Pick(red, blue, green);
    }

    public Game toGame(String line) {
        String[] splitsByColon = line.split(":", 0);

        int gameId = Integer.parseInt(splitsByColon[0].substring(5));

        List<Pick> picks = Arrays.asList(splitsByColon[1].split("; "))
            .stream()
            .map(pick -> toPick(pick))
            .collect(Collectors.toList());

        return new Game(gameId, picks);
    }

    record Pick(int red, int blue, int green) {}

    record Game(int id, List<Pick> picks) {

        public int minimumPossible() {
            int maxRed = picks.stream().mapToInt(Pick::red).max().getAsInt();
            int maxBlue = picks.stream().mapToInt(Pick::blue).max().getAsInt();
            int maxGreen = picks.stream().mapToInt(Pick::green).max().getAsInt();
            return maxRed*maxBlue*maxGreen;
        }

        public boolean isPossible(int maxRed, int maxBlue, int maxGreen) {
            return this.picks.stream().allMatch(pick -> pick.blue <= maxBlue && pick.red <= maxRed && pick.green <= maxGreen);
        }

    }

  

}
