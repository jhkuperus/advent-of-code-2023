package day11.puzzle2;

import day11.puzzle1.GalaxyDistance;
import day11.puzzle1.Input;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

import static day11.puzzle1.GalaxyDistance.findGalaxies;

public class GalaxySuperDistance {

  public static final String PUZZLE = Input.PUZZLE_INPUT;

  public static final char[][] GRID = PUZZLE.lines()
      .map(String::toCharArray)
      .toArray(char[][]::new);

  public static boolean[] isRowExpanded = findEmptyRows();
  public static boolean[] isColExpanded = findEmptyCols();

  public static boolean[] findEmptyCols() {
    var result = new boolean[GRID[0].length];

    for (int x = 0; x < GRID[0].length; x++) {
      var isEmpty = true;

      for (int y = 0; y < GRID.length && isEmpty; y++) {
        isEmpty = GRID[y][x] != '#';
      }

      result[x] = isEmpty;
    }

    return result;
  }

  public static boolean[] findEmptyRows() {
    var result = new boolean[GRID.length];

    for (int y = 0; y < GRID.length; y++) {
      var isEmpty = true;

      for (int x = 0; x < GRID[y].length && isEmpty; x++) {
        isEmpty = GRID[y][x] != '#';
      }

      result[y] = isEmpty;
    }

    return result;
  }

  public static void printMap(char[][] grid) {
    System.out.println(" [ Grid Start ]");
    for (char[] line : grid) {
      System.out.println(line);
    }
    System.out.println(" [ Grid End   ]");
  }

  public static final GalaxyDistance.Galaxy[] galaxies = findGalaxies(GRID);

  public static int countEmptiesBetween(int idxA, int idxB, boolean[] tests) {
    var from  = Math.min(idxA, idxB);
    var to = Math.max(idxA, idxB);

    var result = 0;
    for (int i = from; i < to; i++) {
      result += tests[i] ? 1 : 0;
    }

    return result;
  }

  public static BigInteger shortestPathBetween(GalaxyDistance.Galaxy a, GalaxyDistance.Galaxy b) {
    var yDist = BigInteger.valueOf(Math.abs(a.y() - b.y()));
    var nrOfExpandedRowsBetween = BigInteger.valueOf(countEmptiesBetween(a.y(), b.y(), isRowExpanded));
    yDist = yDist.add(BigInteger.valueOf(1_000_000 - 1).multiply(nrOfExpandedRowsBetween));

    var xDist = BigInteger.valueOf(Math.abs(a.x() - b.x()));
    var nrOfExpandedColsBetween = BigInteger.valueOf(countEmptiesBetween(a.x(), b.x(), isColExpanded));
    xDist = xDist.add(BigInteger.valueOf(1_000_000 - 1).multiply(nrOfExpandedColsBetween));

    return xDist.add(yDist);
  }

  public static void main(String[] args) {
    printMap(GRID);

    var result = BigInteger.ZERO;
    for (int idxA = 0; idxA < galaxies.length - 1; idxA++) {
      for (int idxB = idxA + 1; idxB < galaxies.length; idxB++) {
        final BigInteger shortestPathBetween = shortestPathBetween(galaxies[idxA], galaxies[idxB]);

        System.out.println(STR."Path between Galaxies \{idxA + 1} & \{idxB + 1} == \{shortestPathBetween}");

        result = result.add( shortestPathBetween );
      }
    }

    System.out.println(result);
  }

}
