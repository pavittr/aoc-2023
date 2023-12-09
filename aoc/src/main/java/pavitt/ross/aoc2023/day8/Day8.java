package pavitt.ross.aoc2023.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pavitt.ross.aoc2023.day7.Day7;

public class Day8 {
    public String part1(String fileName) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));

            List<String> parts = Arrays.asList(content.split("\n\n"));
            String[] instructions = parts.get(0).trim().split("");

            Map<String, String> left = Arrays.stream(parts.get(1).split("\n"))
                    .map(Node::toNode)
                    .collect(Collectors.toMap(node -> node.root, node -> node.left));

            Map<String, String> right = Arrays.stream(parts.get(1).split("\n"))
                    .map(Node::toNode)
                    .collect(Collectors.toMap(node -> node.root, node -> node.right));

            String currentNode = "AAA";
            int stepCount = 0;
            String nextInstruction = instructions[0];

            while (!currentNode.equals("ZZZ")) {
                switch (nextInstruction) {
                    case "L":
                        currentNode = left.get(currentNode);
                        break;
                    case "R":
                        currentNode = right.get(currentNode);
                        break;

                }
                stepCount++;
                int nextNumber = stepCount % instructions.length;
                nextInstruction = instructions[nextNumber];
            }

            return "" + stepCount;
        }
    }

    public String part2(String fileName) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));

            List<String> parts = Arrays.asList(content.split("\n\n"));
            String[] instructions = parts.get(0).trim().split("");

            Map<String, String> left = Arrays.stream(parts.get(1).split("\n"))
                    .map(Node::toNode)
                    .collect(Collectors.toMap(node -> node.root, node -> node.left));

            Map<String, String> right = Arrays.stream(parts.get(1).split("\n"))
                    .map(Node::toNode)
                    .collect(Collectors.toMap(node -> node.root, node -> node.right));

            List<String> currentNodes = left.keySet().stream().filter(x -> x.endsWith("A")).toList();

            long answer = currentNodes.stream().map(node -> new Pair(node, findLoopSize(left, right, instructions, node)))
            .map(pair -> factors(pair.count))
            .flatMap (map -> map.entrySet().stream()) 
            .collect(Collectors.groupingBy(x -> x.getKey(), Collectors.summingLong(x -> x.getValue())))
            .keySet()
            .stream()
            .reduce(1L,
                    (sub, e) -> sub * e);
                    

            return "" + answer;
        }
    }

    /**
     * With thanks to https://www.calculatorsoup.com/calculators/math/lcm.php and 
     * https://www.geeksforgeeks.org/print-all-prime-factors-of-a-given-number/ 
     * for pointing me in the direction of hwo to do this by prime factorisation.
     * @param baseNumber
     * @return
     */
   Map<Long, Long> factors(long baseNumber) {
        Map<Long, Long> factorMap = new HashMap<>();
        long number = baseNumber;
        while (number % 2 == 0) {
            number = number / 2;
            factorMap.put(Long.valueOf(2),factorMap.getOrDefault(Long.valueOf(2), 0L) + 1L);
        }

        for (long i = 3; i < ((long) Math.sqrt(baseNumber)) + 1; i = i + 2) {
            while (number % i == 0) {
                number = number / i;
                factorMap.put(Long.valueOf(i), factorMap.getOrDefault(Long.valueOf(i), 0L) + 1L);

            }

        }

        factorMap.put(Long.valueOf(number),  factorMap.getOrDefault(Long.valueOf(number), 0L) + 1L);

        return factorMap;
    }

    record Pair(String node, long count) {
    }

    long findLoopSize(Map<String, String> left, Map<String, String> right, String[] instructions, String startNode) {
        String currentNode = startNode;
        int stepCount = 0;
        String nextInstruction = instructions[0];

        while (!currentNode.endsWith("Z")) {
            switch (nextInstruction) {
                case "L":
                    currentNode = left.get(currentNode);
                    break;
                case "R":
                    currentNode = right.get(currentNode);
                    break;

            }
            stepCount++;
            int nextNumber = stepCount % instructions.length;
            nextInstruction = instructions[nextNumber];
        }

        return stepCount;
    }

    record Node(String root, String left, String right) {
        static Node toNode(String line) {
            // AAA = (BBB, CCC)
            String[] parts = line.split(" +");
            return new Node(parts[0], parts[2].substring(1, 4), parts[3].substring(0, 3));
        }
    }
}
