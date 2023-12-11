package day11.puzzle1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GalaxyDistance {

  public static final String PUZZLE = Input.SAMPLE;

  public static final char[][] GRID = swapRowsCols(expandEmptyRows(swapRowsCols(expandEmptyRows(PUZZLE.lines()
      .map(String::toCharArray)
      .toArray(char[][]::new))))) ;

  public static char[][] swapRowsCols(char[][] input) {
    var result = new char[input[0].length][];
    for (int i = 0; i < result.length; i++) {
      result[i] = new char[input.length];
    }

    for (int y = 0; y < input.length; y++) {
      for (int x = 0; x < input[y].length; x++) {
        result[x][y] = input[y][x];
      }
    }

    return result;
  }

  public static char[][] expandEmptyRows(char[][] input) {
    return Arrays.stream(input)
        .flatMap(l -> {
          var t = new String(l).replaceAll("\\.", " ");
          if (t.isBlank()) {
            return Stream.of(l, Arrays.copyOf(l, l.length));
          } else {
            return Stream.of(l);
          }
        })
        .toArray(char[][]::new);
  }

  public static void printMap(char[][] grid) {
    System.out.println(" [ Grid Start ]");
    for (char[] line : grid) {
      System.out.println(line);
    }
    System.out.println(" [ Grid End   ]");
  }

  public record Galaxy(int id, int x, int y) {}

  public static final Galaxy[] galaxies = findGalaxies(GRID);

  public static Galaxy[] findGalaxies(char[][] grid) {
    var result = new LinkedList<Galaxy>();

    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[0].length; x++) {
        if (grid[y][x] == '#') result.add(new Galaxy(result.size() + 1, x, y));
      }
    }

    return new ArrayList<>(result).toArray(Galaxy[]::new);
  }

  public static long shortestPathBetween(Galaxy a, Galaxy b) {
    var yDist = Math.abs(a.y - b.y);
    var xDist = Math.abs(a.x - b.x);

    return xDist + yDist;
  }

  public static void main(String[] args) {
    printMap(GRID);

    System.out.println(galaxies);

    var result = 0L;
    for (int idxA = 0; idxA < galaxies.length - 1; idxA++) {
      for (int idxB = idxA + 1; idxB < galaxies.length; idxB++) {
        final long shortestPathBetween = shortestPathBetween(galaxies[idxA], galaxies[idxB]);

        System.out.println(STR."Path between Galaxies \{idxA + 1} & \{idxB + 1} == \{shortestPathBetween}");

        result += shortestPathBetween;
      }
    }

    System.out.println(result);
  }

}
