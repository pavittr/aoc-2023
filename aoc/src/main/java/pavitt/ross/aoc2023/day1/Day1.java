package pavitt.ross.aoc2023.day1;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Map.entry;

import java.io.IOException;

public class Day1 {
    public String part1(String fileName) throws IOException {
        Map<String, Integer> lookupPart1 = Map.ofEntries(
                entry("0", 0),
                entry("1", 1),
                entry("2", 2),
                entry("3", 3),
                entry("4", 4),
                entry("5", 5),
                entry("6", 6),
                entry("7", 7),
                entry("8", 8),
                entry("9", 9));
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            return "" + stream.mapToInt(s -> getNumber(s, lookupPart1)).sum();
        }
    }

    public String part2(String fileName) throws IOException {
        Map<String, Integer> additionalDigits = Map.ofEntries(
                entry("0", 0),
                entry("1", 1),
                entry("2", 2),
                entry("3", 3),
                entry("4", 4),
                entry("5", 5),
                entry("6", 6),
                entry("7", 7),
                entry("8", 8),
                entry("9", 9),

                entry("one", 1),
                entry("two", 2),
                entry("three", 3),
                entry("four", 4),
                entry("five", 5),
                entry("six", 6),
                entry("seven", 7),
                entry("eight", 8),
                entry("nine", 9));

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            return "" + stream.mapToInt(s -> getNumber(s, additionalDigits)).sum();
        }
    }

    public int getNumber(String input, Map<String, Integer> lookup) {
        // Start in a single code point
        int firstNumber = lookupForwards(input, 0, lookup);
        if (firstNumber < 0) {
            throw new IllegalStateException("first Number wrong on " + input);
        }

        // Start in a single code point
        int lastNumber = lookupBackwards(input, input.length(), lookup);
        if (lastNumber < 0) {
            throw new IllegalStateException("last number wrong on " + input);
        }

        return Integer.parseInt("" + firstNumber + lastNumber);
    }

    public Integer lookupForwards(String input, int endNumber, Map<String, Integer> lookup) {
        if (endNumber > input.length()) {
            return -1;
        }

        String toSearch = input.substring(0, endNumber);

        Optional<Entry<String, Integer>> optEntry = lookup.entrySet().stream()
                .filter(mapEntry -> toSearch.contains(mapEntry.getKey())).findFirst();
        if (optEntry.isPresent()) {
            return optEntry.get().getValue();
        }

        return lookupForwards(input, endNumber + 1, lookup);
    }

    public Integer lookupBackwards(String input, int startNumber, Map<String, Integer> lookup) {
        if (startNumber < 0) {
            return -1;
        }

        String toSearch = input.substring(startNumber, input.length());

        Optional<Entry<String, Integer>> optEntry = lookup.entrySet().stream()
                .filter(mapEntry -> toSearch.contains(mapEntry.getKey())).findFirst();
        if (optEntry.isPresent()) {
            return optEntry.get().getValue();
        }

        return lookupBackwards(input, startNumber - 1, lookup);
    }

}
