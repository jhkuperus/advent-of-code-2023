package day13.puzzle1;

import day11.puzzle1.GalaxyDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Mirrors {

  public static final List<String> INPUT = Input.PUZZLE.lines().collect(Collectors.toList());

  public static List<char[][]> collectGrids() {
    var result = new ArrayList<char[][]>();
    var temp = new ArrayList<String>();

    for (String line : INPUT) {
      if (line.isBlank()) {
        result.add(temp.stream()
            .map(String::toCharArray)
            .toArray(char[][]::new));
        temp.clear();
        continue;
      }

      temp.add(line);
    }

    result.add(temp.stream()
        .map(String::toCharArray)
        .toArray(char[][]::new));

    return result;
  }

  public static final List<char[][]> asRows = collectGrids();

  public static final List<char[][]> asCols = asRows.stream()
      .map(GalaxyDistance::swapRowsCols)
      .collect(Collectors.toList());

  public record MirrorLine(int position, int size) {}

  public static int findMirrorLine(char[][] grid) {
    for (int i = 0; i < grid.length - 1; i++) {
      if (findMirrorSize(grid, i) > 0) {
        return i + 1;
      }
    }

    return -1;
  }

  public static int findMirrorSize(char[][] grid, int mirrorAfter) {
    var size = 0;

    for (int i = 0; (mirrorAfter - i) >= 0 && (i + mirrorAfter + 1) < grid.length; i++) {
      if (Arrays.equals(grid[mirrorAfter - i], grid[mirrorAfter + i + 1])) {
        size++;
      } else {
        return -1;
      }
    }

    return size;
  }

  public static void main(String[] args) {
    long result = 0;
    for (int idx = 0; idx < asRows.size(); idx++) {
      var horizontalMirror = findMirrorLine(asRows.get(idx));
      var verticalMirror = findMirrorLine(asCols.get(idx));

      for (int y = 0; y < asRows.get(idx).length; y++) {
        System.out.println(asRows.get(idx)[y]);
      }
      System.out.println(STR."Horizontal mirror: \{horizontalMirror}");
      System.out.println(STR."Vertical mirror: \{verticalMirror}");

      if (verticalMirror > 0) {
        result += verticalMirror;
      }
      if (horizontalMirror > 0) {
        result += (100 * (horizontalMirror));
      }
    }

    System.out.println(result);
  }

}
