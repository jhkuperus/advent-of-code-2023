package day14.puzzle1;

import day10.puzzle1.PipeMaze;

import java.math.BigInteger;
import java.util.ArrayList;

import static day10.puzzle1.PipeMaze.printMaze;

public class RollingRocks {

  public static final String INPUT = Input.PUZZLE;

  public static char[][] grid = INPUT.lines()
      .map(String::toCharArray)
      .toArray(char[][]::new);

  public static final Space[][] spaces = parseGrid();

  public static final Rock[] rocks = findRocks();

  public static Space[][] parseGrid() {
    var result = new Space[grid.length][grid[0].length];

    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[0].length; x++) {
        result[y][x] = Space.parse(x, y, grid[y][x]);
      }
    }

    return result;
  }

  public static Rock[] findRocks() {
    var result = new ArrayList<Rock>();

    for (int y = 0; y < spaces.length; y++) {
      for (int x = 0; x < spaces[y].length; x++) {
        if (spaces[y][x].getRock() != null) {
          result.add(spaces[y][x].getRock());
        }
      }
    }

    return result.toArray(Rock[]::new);
  }

  public record Rock(int x, int y) {}

  public static void rollEverythingNorthInCol(int x) {
    var previousFree = -1;

    for (int y = 0; y < grid.length; y++) {
      if (grid[y][x] == '.' && previousFree == -1) {
        // Set next free spot
        previousFree = y;
      }

      if (grid[y][x] == 'O') {
        if (previousFree >= 0) {
          grid[previousFree][x] = 'O';
          grid[y][x] = '.';
          previousFree++;
        } else {
          previousFree = -1;
        }
      }

      if (grid[y][x] == '#') {
        previousFree = -1;
      }
    }
  }

  public static BigInteger countWeightInCol(int x) {
    var result = BigInteger.ZERO;

    for (int y = 0; y < grid.length; y++) {
      if (grid[y][x] == 'O') {
        result = result.add(BigInteger.valueOf(grid.length - y));
      }
    }

    return result;
  }

  public static void main(String[] args) {
    printMaze(grid);

    for (int x = 0; x < grid[0].length; x++) {
      rollEverythingNorthInCol(x);
    }

    printMaze(grid);

    var result = BigInteger.ZERO;

    for (int x = 0; x < grid[0].length; x++) {
      result = result.add(countWeightInCol(x));
    }

    System.out.println(result);
  }

}
