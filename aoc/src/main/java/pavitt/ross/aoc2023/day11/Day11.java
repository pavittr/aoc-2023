package pavitt.ross.aoc2023.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day11 {

    public String part1(String fileName) throws IOException {
        List<Coord> galaxies = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        int ymax = lines.size();
        int xmax = lines.get(0).length();
        for (int i = 0; i < lines.size(); i++) {
            String thisLine = lines.get(i);
            String[] columns = thisLine.split("");
            for (int j = 0; j < columns.length; j++) {
                if (columns[j].equals("#")) {
                    galaxies.add(new Coord(j, i));
                }

            }
        }

        // `` find the blank lines but don't do anythign with them
        List<Integer> blankColumns = new ArrayList<>();
        for (int x = 0; x < xmax; x++) {
            int xVal = x;
            if (IntStream.range(0, ymax).noneMatch(yVal -> galaxies.contains(new Coord(xVal, yVal)))) {
                blankColumns.add(x);
            }
        }
        System.out.println("Blank columsn " + blankColumns);

        // `` find the blank lines but don't do anythign with them
        List<Integer> blankRows = new ArrayList<>();
        for (int y = 0; y < ymax; y++) {
            int yVal = y;
            if (IntStream.range(0, xmax).noneMatch(xVal -> galaxies.contains(new Coord(xVal, yVal)))) {
                blankRows.add(y);
            }
        }
        System.out.println("Blank rows " + blankRows);

        // for each galaxy, work out its distance to other galaxies
        // for each balnk colum crossed, add 1
        // for each blank row crossed, add 1

        long sum = 0;
        for ( Coord galaxy1 : galaxies) {
            for (Coord galaxy2 : galaxies) {
                if (galaxy1.equals(galaxy2)) {
                    continue;
                }
                Lines lineCollection = galaxy1.stepDistance(galaxy2);
                // find all the crossedColumsn that intersect witht he blank columns
             //   System.out.println("Galaxies " + galaxy1 + ":" + galaxy2+" have lines " + lineCollection.representedDistance(blankColumns, blankRows, 10));
                sum += lineCollection.representedDistance(blankColumns, blankRows, 1000000);
               
            }

        }






        return "" + (sum / 2);
    }

    record Lines(List<Integer> crossedColumns, List<Integer> crossedRows) {
        long representedDistance(List<Integer> blankColumns, List<Integer> blankRows, int expansionFactor) {
 long extraCOlumns = crossedColumns.stream().filter(crossedColumn -> blankColumns.contains(crossedColumn)).count();
 long extraRows = crossedRows.stream().filter(crossedRow -> blankRows.contains(crossedRow)).count();
            return crossedColumns.size() + crossedRows.size() + (extraCOlumns * (expansionFactor-1)) + (extraRows * (expansionFactor-1));

        }
    }

    record Coord(int x, int y) {

        Lines stepDistance(Coord that) {
            List<Integer> crossedColumns = new ArrayList<>();
            if (this.x > that.x) {
                crossedColumns = IntStream.range(that.x, this.x).boxed().toList();
            } else {
                crossedColumns = IntStream.range(this.x, that.x).boxed().toList();
            }
            List<Integer> crossedRows = new ArrayList<>();
            if (this.y > that.y) {
                crossedRows = IntStream.range(that.y, this.y).boxed().toList();
            } else {
                crossedRows = IntStream.range(this.y, that.y).boxed().toList();
            }

            return new Lines(crossedColumns, crossedRows);



            
        }
    }
}
