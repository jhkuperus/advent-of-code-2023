package day10.puzzle1;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static day10.puzzle1.Input.PUZZLE_INPUT;
import static day10.puzzle1.Input.SAMPLE;

public class PipeMaze {

  public static final String PUZZLE_TO_RUN = PUZZLE_INPUT;

  public static final char[][] GRID = PUZZLE_TO_RUN.lines()
      .map(line -> line.replaceAll("L", "╚")
          .replaceAll("J", "╝")
          .replaceAll("7", "╗")
          .replaceAll("F", "╔")
          .replaceAll("\\|", "║")
          .replaceAll("-", "═"))
      .map(String::toCharArray)
      .toArray(char[][]::new);


  /*
    | is a vertical pipe connecting north and south.
    - is a horizontal pipe connecting east and west.
    L is a 90-degree bend connecting north and east.                             S=
    J is a 90-degree bend connecting north and west.                             |
    7 is a 90-degree bend connecting south and west.
    F is a 90-degree bend connecting south and east.
    . is ground; there is no pipe in this tile.
    S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
   */
  public static void printMaze() {
    for (char[] line : GRID) {
      System.out.println(line);
    }
  }

  public static Coord findStart() {
    int y = 0, x = 0;


    search: for (; y < GRID.length; y++) {
      for (x = 0; x < GRID[y].length; x++) {
        if (GRID[y][x] == 'S') {
          break search;
        }
      }
    }

    Dir[] connectionsFromStart = new Dir[2];
    int connFound = 0;
    for (Dir dir : Dir.values()) {
      var c = dir.characterInThisDirectionFrom(x, y);

      if (c.isPresent()) {
        if (exits.containsKey(c.get()) && exits.get(c.get()).hasExit(dir.inverse())) {
          connectionsFromStart[connFound++] = dir;
        }

        if (connFound == 2) {
          GRID[y][x] = inverse.get(new Pair(connectionsFromStart[0], connectionsFromStart[1]));
          return new Coord(x, y, null);
        }
      }
    }

    throw new IllegalArgumentException("Cannot find a proper start!");
  }

  public enum Dir {
    NORTH(x -> x, y -> y - 1),
    EAST(x -> x + 1, y -> y),
    SOUTH(x -> x, y -> y + 1),
    WEST(x -> x - 1, y -> y);

    public static final Map<Dir, Dir> inverseMap = Map.of(
        NORTH, SOUTH,
        EAST, WEST,
        SOUTH, NORTH,
        WEST, EAST
    );

    private final Function<Integer, Integer> xMod;
    private final Function<Integer, Integer> yMod;

    Dir(Function<Integer, Integer> xMod, Function<Integer, Integer> yMod) {
      this.xMod = xMod;
      this.yMod = yMod;
    }

    public Dir inverse() {
      return inverseMap.get(this);
    }

    public Coord moveToFrom(Coord old) {
      old.x = this.xMod.apply(old.x);
      old.y = this.yMod.apply(old.y);
      old.from = this.inverse();
      return old;
    }

    public Optional<Character> characterInThisDirectionFrom(int x, int y) {
      final Integer newY = this.yMod.apply(y);
      final Integer newX = this.xMod.apply(x);

      if (newY < 0 || newY >= GRID.length) {
        return Optional.empty();
      }
      if (newX < 0 || newX >= GRID[y].length) {
        return Optional.empty();
      }
      return Optional.of(GRID[newY][newX]);
    }
  }

  record Pair(Dir a, Dir b) {
    public Dir otherThan(Dir other) {
      return a == other ? b : a;
    }

    public boolean hasExit(Dir q) {
      return a == q || b == q;
    }
  }

  static Map<Character, Pair> exits = Map.of(
      '║', new Pair(Dir.NORTH, Dir.SOUTH),
      '═', new Pair(Dir.EAST, Dir.WEST),
      '╚', new Pair(Dir.NORTH, Dir.EAST),
      '╝', new Pair(Dir.NORTH, Dir.WEST),
      '╗', new Pair(Dir.SOUTH, Dir.WEST),
      '╔', new Pair(Dir.EAST, Dir.SOUTH)
  );

  static Map<Pair, Character> inverse = exits.entrySet()
      .stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


  public static class Coord {
    public int x, y;
    public Dir from;

    public Coord(int x, int y, Dir from) {
      this.x = x;
      this.y = y;
      this.from = from;
    }
  }

  public static class PuzzleSolver {
    public Coord leftPos;
    public Coord rightPos;
    public int[] leftDistance = new int[]{0};
    public int[] rightDistance = new int[]{0};

    public PuzzleSolver(int startX, int startY) {
      var exitsOfStart = exits.get(GRID[startY][startX]);
      var directionLeft = exitsOfStart.a;
      leftPos = directionLeft.moveToFrom(new Coord(startX, startY, null));

      var directionRight = exitsOfStart.b;
      rightPos = directionRight.moveToFrom(new Coord(startX, startY, null));

      GRID[startY][startX] = '*';
    }

    public int findLongestDistanceFromStart() {
      var advancedLeft = true;
      var advancedRight = true;
      while (advancedLeft || advancedRight) {
        advancedLeft = advanceFrom(leftPos, leftDistance);
        advancedRight = advanceFrom(rightPos, rightDistance);
      }

      return Math.max(leftDistance[0], rightDistance[0]);
    }

    private boolean advanceFrom(Coord start, int[] distanceCounter) {
      final char pipeChar = GRID[start.y][start.x];
      if (!exits.containsKey(pipeChar)) {
        // We have moved to a position where there's another character than a pipe, so either we went outside a pipe, or
        // we touched an already visited position
        return false;
      }

      var availableExit = exits.get(pipeChar).otherThan(start.from);
      GRID[start.y][start.x] = '*';
      availableExit.moveToFrom(start);
      distanceCounter[0]++;
      return true;
    }

  }

  public static void main(String[] args) {
    printMaze();

    final Coord start = findStart();
    final PuzzleSolver solver = new PuzzleSolver(start.x, start.y);
    var longestDistance = solver.findLongestDistanceFromStart();

    printMaze();

    System.out.println(longestDistance);
  }

}
