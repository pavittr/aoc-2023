package pavitt.ross.aoc2023.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
    public String part1(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            Schematic scehmatic = parseScehmatic(stream.collect(Collectors.toList()));
            List<Part> realParts = scehmatic.findRealParts();
            return "" + realParts.stream().mapToInt(Part::value).sum();
        }
    }

    public String part2(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            Schematic scehmatic = parseScehmatic(stream.collect(Collectors.toList()));
            return "" + scehmatic.gearSum();
        }
    }

    Schematic parseScehmatic(List<String> lines) {
        List<Part> parts = new ArrayList<>();
        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            String currentPart = "";
            int startx = -1;
            for (int j = 0; j < currentLine.length(); j++) {
                if (currentLine.charAt(j) == '.') {
                    if (currentPart.length() > 0) {
                        parts.add(new Part(startx, i, j - startx, Integer.parseInt(currentPart)));
                        startx = -1;
                        currentPart = "";
                    }
                } else if (Character.isDigit(currentLine.charAt(j))) {
                    if (startx < 0) {
                        // New number
                        startx = j;
                        currentPart = "" + currentLine.charAt(j);
                    } else {
                        currentPart = currentPart + currentLine.charAt(j);
                    }
                } else {
                    symbols.add(new Symbol(j, i, "" + currentLine.charAt(j)));
                    if (currentPart.length() > 0) {
                        parts.add(new Part(startx, i, j - startx, Integer.parseInt(currentPart)));
                        startx = -1;
                        currentPart = "";
                    }
                }
            }
            if (currentPart.length() > 0) {
                parts.add(new Part(startx, i, currentLine.length() - startx, Integer.parseInt(currentPart)));
            }
        }
        return new Schematic(symbols, parts);
    }

    record Schematic(List<Symbol> symbols, List<Part> parts) {
        List<Part> findRealParts() {
            return parts.stream().filter(part -> part.isReal(symbols)).collect(Collectors.toList());
        }

        int gearSum() {
            return symbols.stream()
            .filter(symbol -> symbol.icon.equalsIgnoreCase("*"))
            .mapToInt(symbol -> symbol.gearRatio(parts))
            .sum();
        }
    }

    record Symbol(int x, int y, String icon) {
        int gearRatio(List<Part> potentialParts) {
            List<Part> gearParts = potentialParts.stream().filter(part -> this.isNear(part)).toList();
            if (gearParts.size() == 2) {
                return gearParts.get(0).value * gearParts.get(1).value;
            }
            return 0;
        }

        boolean isNear(Part part) {
            boolean xIsInside = x >= part.xStart - 1 && x <= part.xStart + part.length;
            boolean yIsInside = y >= part.yStart - 1 && y <= part.yStart + 1;
            return xIsInside && yIsInside;
        }
    }

    record Coord(int x, int y) {
    }

    record Part(int xStart, int yStart, int length, int value) {
        boolean isReal(List<Symbol> symbols) {
            return symbols.stream().anyMatch(symbol -> symbol.isNear(this));
        }
    }
}
