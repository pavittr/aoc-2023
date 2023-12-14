package pavitt.ross.aoc2023.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day10 {
    public String part1(String fileName) throws IOException {
        Map<Coord, PipeType> grid = new HashMap<>();

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (int i = 0; i < lines.size(); i++) {
            String thisLine = lines.get(i);
            String[] columns = thisLine.split("");
            for (int j = 0; j < columns.length; j++) {
                grid.put(new Coord(j, i), PipeType.find(columns[j]));
            }
        }

        // fidn the start
        Coord start = grid
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(PipeType.START))
                .map(entry -> entry.getKey())
                .findFirst()
                .get();

        // Look around the Start square and see what is viable
        List<Coord> starters = findStartConnections(grid, start);

        // Chase from the start position in each direction
        int stepCount = 1;

        Coord route1Previous = start;
        Coord route2Previous = start;
        Coord route1 = starters.get(0);
        Coord route2 = starters.get(1);
        while (!route1.equals(route2)) {
            Coord currentRoute1 = findConnector(route1Previous, route1, grid.get(route1));
            Coord currentRoute2 = findConnector(route2Previous, route2, grid.get(route2));
            stepCount++;
            route1Previous = route1;
            route2Previous = route2;
            route1 = currentRoute1;
            route2 = currentRoute2;
        }
        return "" + stepCount;
    }

    public String part2(String fileName) throws IOException {
        Map<Coord, PipeType> grid = new HashMap<>();

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (int i = 0; i < lines.size(); i++) {
            String thisLine = lines.get(i);
            String[] columns = thisLine.split("");
            for (int j = 0; j < columns.length; j++) {
                grid.put(new Coord(j, i), PipeType.find(columns[j]));
            }
        }

        // fidn the start
        Coord start = grid
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(PipeType.START))
                .map(entry -> entry.getKey())
                .findFirst()
                .get();

        // Look around the Start square and see what is viable
        List<Coord> starters = findStartConnections(grid, start);

        // Chase from the start position in each direction
        int stepCount = 1;

        Coord route1Previous = start;
        Coord route2Previous = start;
        Coord route1 = starters.get(0);
        Coord route2 = starters.get(1);
        List<Coord> route = new ArrayList<>();
        route.add(start);
        route.add(route1);
        route.add(route2);
        while (!route1.equals(route2)) {
            Coord currentRoute1 = findConnector(route1Previous, route1, grid.get(route1));
            Coord currentRoute2 = findConnector(route2Previous, route2, grid.get(route2));
            if (!currentRoute1.equals(currentRoute2)) {
                route.add(currentRoute2);
            }
            route.add(currentRoute1);

            stepCount++;
            route1Previous = route1;
            route2Previous = route2;
            route1 = currentRoute1;
            route2 = currentRoute2;
        }

        int ymax = lines.size();
        int xmax = lines.get(0).length();
        for (int y = 0; y < ymax; y++) {
            for (int x = 0; x < xmax; x++) {
                if (route.contains(new Coord(x, y))) {

                    System.out.print(grid.get(new Coord(x, y)).boxChar);
                } else {
                    System.out.print(".");
                }

            }
            System.out.println(" ");

        }

        return "" + stepCount;
    }

    List<Coord> findStartConnections(Map<Coord, PipeType> grid, Coord current) {
        Map<Coord, List<PipeType>> viableOptions = Map.of(
                new Coord(current.x - 1, current.y),
                List.of(PipeType.NTOE, PipeType.STOE, PipeType.ETOW),
                new Coord(current.x + 1, current.y),
                List.of(PipeType.STOW, PipeType.NTOW, PipeType.ETOW),
                new Coord(current.x, current.y - 1),
                List.of(PipeType.NTOS, PipeType.STOE, PipeType.STOW),
                new Coord(current.x, current.y + 1),
                List.of(PipeType.NTOS, PipeType.NTOW, PipeType.NTOE));

        return viableOptions.entrySet().stream()
                .filter(entry -> grid.containsKey(entry.getKey()))
                .filter(entry -> entry.getValue().contains(grid.get(entry.getKey())))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    Coord findConnector(Coord entry, Coord current, PipeType thisType) {
        switch (thisType) {
            case PipeType.NTOS:
                if (current.y > entry.y) {
                    return new Coord(current.x, current.y + 1);
                } else {
                    return new Coord(current.x, current.y - 1);
                }
            case PipeType.ETOW:
                if (current.x > entry.x) {
                    return new Coord(current.x + 1, current.y);
                } else {
                    return new Coord(current.x - 1, current.y);
                }
            case PipeType.NTOE:
                if (current.y > entry.y) {
                    return new Coord(current.x + 1, current.y);
                } else {
                    return new Coord(current.x, current.y - 1);
                }
            case PipeType.NTOW:
                if (current.y > entry.y) {
                    return new Coord(current.x - 1, current.y);
                } else {
                    return new Coord(current.x, current.y - 1);
                }
            case PipeType.STOW:
                if (current.y < entry.y) {
                    return new Coord(current.x - 1, current.y);
                } else {
                    return new Coord(current.x, current.y + 1);
                }
            case PipeType.STOE:
                if (current.y < entry.y) {
                    return new Coord(current.x + 1, current.y);
                } else {
                    return new Coord(current.x, current.y + 1);
                }
            default:
                throw new IllegalStateException(
                        "Unrecognised type " + thisType + " against entry " + entry + " and current " + current);
        }
    }

    record Coord(int x, int y) {
    }

    enum PipeType {
        NTOS("|", "" + '\u2502'),
        ETOW("-", "" + '\u2500'),
        NTOE("L", "" + '\u2514'),
        NTOW("J", "" + '\u2518'),
        STOW("7", "" + '\u2510'),
        STOE("F", "" + '\u250C'),
        GROUND(".", "."),
        START("S", "S");

        String icon;
        String boxChar;

        PipeType(String icon, String boxChar) {
            this.icon = icon;
            this.boxChar = boxChar;
        }

        static PipeType find(String searchIcon) {
            return Arrays.stream(PipeType.values())
                    .filter(type -> type.icon.equalsIgnoreCase(searchIcon))
                    .findFirst()
                    .get();
        }
    }
}
