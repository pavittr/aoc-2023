package pavitt.ross.aoc2023.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day12 {
    public String part1(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<Line> lines = stream.map(Line::toLine).toList();
            //lines.forEach(System.out::println);
            // break the input into sections
            // Rues:
            // 1. A section cannot have mre damage in it than it has length
            // 2. If a section has two or more damaged sections 
            // Possible cobinations"
            // ... - everythig is operationa;
            // ### - everythign is damaged
            // ..# - this one is operational, but its right neightbour is damaged
            // #.. - this one is operational, but its left neighbour is damaged
            // ##. - this one and its left neighbour are damaged, but its right neighbour is operational
            // .## - this one and its right neighbour are damaged, but its left neighbour is operational
            // .#. - this one is damaged but both its neighbours are operationsal
            // #.# - this one is operational but noth its neighbours are damaged

            //System.out.println(lines.stream().map(line -> line.generateCombinations().stream().filter(Line::validate).count()).toList());
            //lines.get(1).generateCombinations().stream().forEach(line -> System.out.println(line.originalPattern));


            // The list of 

            return "" + lines.stream().mapToLong(line -> line.generateCombinations().stream().filter(Line::validate).count()).sum();
        }
    }

        public String part2(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<Line> lines = stream.map(line -> Line.toLine(expand(line))).toList();


            return "" + lines.parallelStream().mapToLong(line -> line.generateCombinations().parallelStream().filter(Line::validate).count()).sum();
        }
    }

    String expand(String line) {
        String[] parts = line.split(" +");;
        return parts[0].repeat(5) + " " + parts[1].repeat(5);
    }

    record Line(List<Section> sections, List<Integer> damagedAreas, String originalPattern, String originalDamagedAreas) {
        static Line toLine(String line) {
            String[] parts = line.split(" +");
            String[] pattern = parts[0].split("");
            // break the pattern into continuous chunks

            List<Section> sections = new ArrayList<>(0);

            int currentStart = 0;
            int currentEnd = 0;
            Status currentStatus = Status.toStatus(pattern[0]);

            if (pattern[0].equalsIgnoreCase(pattern[1])) {
                currentEnd++;
            } else {
                // The first two differ so mark pattern9]0 as a first Section
                sections.add(new Section(currentStart, currentEnd - currentStart + 1, currentStatus));
                currentStart = 1;
                currentEnd = 1;
                currentStatus = Status.toStatus(pattern[currentStart]);
            }

            for (int i = 2; i < pattern.length - 1; i++) {
                if (pattern[i-1].equalsIgnoreCase(pattern[i])) {
                    currentEnd++;
                } else {
                    // The first two differ so mark pattern9]0 as a first Section
                    sections.add(new Section(currentStart, currentEnd-currentStart + 1, currentStatus));
                    currentStart = i;
                    currentEnd = i;
                    currentStatus = Status.toStatus(pattern[i]);
                }
            }
            // Also need to check if the last one was a change
            if (pattern[pattern.length - 2].equalsIgnoreCase(pattern[pattern.length - 1])) {

                currentEnd++;
                sections.add(new Section(currentStart, currentEnd - currentStart + 1, currentStatus));
            } else {
                // The last two differ so mark the secodn to last section and then create a section for the last one
                sections.add(new Section(currentStart, currentEnd - currentStart + 1, currentStatus));
                sections.add(new Section(currentEnd + 1, 1, Status.toStatus(pattern[pattern.length - 1])));


            }

            return new Line(sections, Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).boxed().toList(), parts[0], parts[1]);
        }

        /**
         * Checks if this is feasible. 
         * @return true if the pattern matches the damaged pipe sections, false otherwise
         */
        boolean validate() {
            // For thsi to match, expand it to a fitting pattern
            String stringMatch = damagedAreas.stream().map(damagedArea -> "#".repeat(damagedArea)).collect(Collectors.joining("\\.+"));
            return originalPattern.matches("\\.*" + stringMatch + "\\.*");
        }

        List<Line> generateCombinations() {
            int unknowns = sections.stream().filter(section -> section.status.equals(Status.UNKNOWN)).mapToInt(section -> Integer.valueOf(section.length)).sum();
            // // 2^32 = 1L << 32 = (long) Math.pow(2, 32)
            long combinations = 1L << unknowns;
            List<Line> responses = new ArrayList<>();
            for (long i = 0; i < combinations; i ++) {
                // Turn the long into a binary string
                String binaryString = Long.toBinaryString(i);
                
                Deque<String> characterPump = new ArrayDeque<>(Arrays.asList(binaryString.replaceAll("0", "#").replaceAll("1", ".").split("")));
                int missingElements = unknowns - characterPump.size();
                if (missingElements > 0) {
                    IntStream.range(0, missingElements).forEach(unised -> characterPump.addFirst("#"));
                }

                String rebuiltLine = Arrays.stream(originalPattern.split("")).map(character -> character.equalsIgnoreCase("?") ?  characterPump.removeFirst(): character).collect(Collectors.joining("")) + " " + originalDamagedAreas ;
                responses.add(Line.toLine(rebuiltLine));
                
            }
            return responses;



        }
    }



    record Section(int start, int length, Status status) {

    }

    enum Status {
        OPERATIONAL("."),
        DAMAGED("#"),
        UNKNOWN("?");

        String icon;

        Status(String icon) {
            this.icon = icon;
        }

        static Status toStatus(String search) {
            return Arrays.stream(Status.values()).filter(status -> status.icon.equalsIgnoreCase(search)).findFirst()
                    .get();
        }
    }
}
