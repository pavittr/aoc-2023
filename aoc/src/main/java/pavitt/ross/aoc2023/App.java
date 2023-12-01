package pavitt.ross.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;

import pavitt.ross.aoc2023.day1.Day1;



/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException  {
        Day1 d1 = new Day1();
        System.out.println("Part 1:" + d1.part1("inputs/day1.test"));
        System.out.println("Part 2:" + d1.part2("inputs/day1Part2.test"));
        System.out.println("Part 1:" + d1.part1("inputs/day1.prod"));
        System.out.println("Part 2:" + d1.part2("inputs/day1.prod"));
    
    }

}
