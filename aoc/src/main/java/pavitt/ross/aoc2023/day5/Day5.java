package pavitt.ross.aoc2023.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;


public class Day5 {

    public String part1(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));

        List<String> parts = Arrays.asList(content.split("\n\n"));
        String seedList = parts.get(0).substring(7);
        MapCollection mappers = new MapCollection( parts.stream().skip(1).map(part -> toMapper(part)).toList());

        return "" + Arrays.stream(seedList.split(" +"))
        .mapToLong(Long::parseLong)
        .map(seed -> mappers.map(seed))
        .min().getAsLong();
    }

    public String part2(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));

        List<String> parts = Arrays.asList(content.split("\n\n"));
        List<SeedPair> seedPairs = toSeedList(parts.get(0).substring(7));
        MapCollection mappers = new MapCollection( parts.stream().skip(1).map(part -> toMapper(part)).toList());


        List<Long> outcomes = new ArrayList<>();
        for (SeedPair seedpair : seedPairs) {
            // LongStream.range(seedpair.start, seedpair.end)
            // .forEach(number -> System.out.println("Producing " + number));
            Long outcome = LongStream.range(seedpair.start, seedpair.end)
            .parallel()
            .map(mappers::map)
            .min()
            .getAsLong();
            outcomes.add(outcome);
            System.out.println("Pair " + seedpair + " found " + outcome);
        }


       
        return "" + outcomes.stream().mapToLong(Long::valueOf).min().getAsLong(); 
    }


    record SeedPair(long start, long end) {

    }


    List<SeedPair> toSeedList(String seedlist) {
        long[] seedArray = Arrays.stream(seedlist.split(" +"))
        .mapToLong(Long::parseLong)
        .toArray();
        List<SeedPair> seedResponse = new ArrayList<>();

        for (int i = 0; i <= seedArray.length / 2; i = i + 2) {
            seedResponse.add(new SeedPair(seedArray[i], seedArray[i] + seedArray[i+1]));
        }
        return seedResponse;

    }


    record MapCollection(List<Mapper> mappers) {
        long map(long seed) {
           // System.out.println("Mapping " + seed);
            long response = seed;
            for (Mapper mapper : mappers) {
                response = mapper.map(response);
                //System.out.println("Mapped to " + response);

            }
            return response;
        }
    }

    Mapper toMapper(String line) {
        List<String> lines = Arrays.asList(line.split("\n"));
        return new Mapper(lines.stream().skip(1).map(thisLine -> toMapRule(thisLine)).toList());
    }

    MapRule toMapRule(String line) {
        long[] parts = Arrays.stream(line.split(" +")).mapToLong(Long::parseLong).toArray();
        return new MapRule(parts[0], parts[1], parts[2]);
    }

    record MapRule(long dst, long src, long range) {
        boolean isInRange(long thisSrc) {
            return thisSrc >= src && thisSrc < (src + range);
        }

        long map(long thisSrc) {
            // the amount you add on to the dest is defined by hwo far through the src range you are in
            long additional = thisSrc - src;
            return dst + additional;
        }
    }

    record Mapper(List<MapRule> rules) {
        long map(long thisSeed) {
            return rules.stream()
            .filter(rule -> rule.isInRange(thisSeed))
            .mapToLong(rule -> rule.map(thisSeed))
            .findFirst()
            .orElse(thisSeed);
        }
    }

    
    
}
