package pavitt.ross.aoc2023.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day6 {
    public String part1(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        List<Long> times = Arrays.stream(lines.get(0).substring("time:".length()).trim().split(" +"))
                .map(Long::parseLong).toList();
        List<Long> distances = Arrays.stream(lines.get(1).substring("Distance:".length()).trim().split(" +"))
                .map(Long::parseLong).toList();
        List<Race> races = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            races.add(new Race(times.get(i), distances.get(i)));
        }

        return ""
                + races.stream().mapToLong(race -> race.getPossiblePressLength()).reduce(1, (sub, elem) -> sub * elem);
    }

    public String part2(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Long time = Long.parseLong(lines.get(0).substring("time:".length()).trim().replaceAll(" ", ""));
        Long distance = Long.parseLong(lines.get(1).substring("Distance:".length()).trim().replaceAll(" ", ""));

        

        return "" + new Race(time, distance).getPossiblePressLength();
    }

    record Race(long time, long distance) {
        long getPossiblePressLength() {
            long discriminant = (time * time) - 4 * distance;
            long lower = quadraticLower(discriminant);
            long upper = quadraticUpper(discriminant);
            System.out.println("For " + time + ":" + distance + ", roots are (" + lower + "," + upper + ")");
            return upper - lower + 1;

        }

        long quadraticLower(long discriminant) {
            double tpressedN = (time - Math.sqrt(discriminant)) * 0.5;
            return ((long) tpressedN) + 1;
        }

        long quadraticUpper(long discriminant) {
            double tpressed = (time + Math.sqrt(discriminant)) * 0.5;
            if ((tpressed - (long) tpressed) < 0.000001) {
                return ((long) tpressed) - 1;
            }
            return (long) tpressed;
        }

    }

}
