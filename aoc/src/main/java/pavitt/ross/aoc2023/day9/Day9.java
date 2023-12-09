package pavitt.ross.aoc2023.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class Day9 {
    public String part1(String fileName) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return "" + stream.map(line -> parse(line)).map(ints -> reduce(ints))
                    .map(ints -> ints[ints.length - 1])
                    .mapToInt(Integer::valueOf)
                    .sum();
        }
    }

    public String part2(String fileName) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return "" + stream.map(line -> parse(line)).map(ints -> backwards(ints))
                    .map(ints -> ints[0])
                    .mapToInt(Integer::valueOf)
                    .sum();
        }
    }

    int[] parse(String line) {
        return Arrays.stream(line.split(" +")).mapToInt(Integer::parseInt).toArray();
    }

    int[] backwards(int[] input) {
        int[] result = new int[input.length - 1];
        for (int i = 0; i < input.length - 1; i++) {
            result[i] = input[i + 1] - input[i];
        }
        if (Arrays.stream(result).distinct().count() == 1) {

            // We hit the bottom, this is the number you need to return
            int[] response = new int[input.length + 1];
            for (int i = 0; i < input.length; i++) {
                response[i + 1] = input[i];

            }
            response[0] = response[1] - result[0];
            return response;
        }

        // ELse this is somewhere int he middle, let's ask the layer below, and then use
        // its first number to add one on to ourselves
        int[] response = new int[input.length + 1];
        for (int i = 0; i < input.length; i++) {
            response[i + 1] = input[i];
        }

        int[] reducedSet = backwards(result);

        response[0] = response[1] - reducedSet[0];
        return response;
    }

    int[] reduce(int[] input) {
        int[] result = new int[input.length - 1];
        for (int i = 0; i < input.length - 1; i++) {
            result[i] = input[i + 1] - input[i];
        }
        if (Arrays.stream(result).distinct().count() == 1) {

            // We hit the bottom, this is the number you need to return
            int[] response = new int[input.length + 1];
            for (int i = 0; i < input.length; i++) {
                response[i] = input[i];

            }
            response[response.length - 1] = response[response.length - 2] + result[0];
            return response;
        }

        // ELse this is somewhere int he middle, let's ask the layer below, and then use
        // its last number to add one on to ourselves
        int[] response = new int[input.length + 1];
        for (int i = 0; i < input.length; i++) {
            response[i] = input[i];
        }

        int[] reducedSet = reduce(result);

        response[response.length - 1] = response[response.length - 2] + reducedSet[reducedSet.length - 1];
        return response;
    }
}
