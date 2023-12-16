package day10.puzzle1;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static day10.puzzle1.Input.*;

public class PipeMaze {

//  public static final String PUZZLE_TO_RUN = SAMPLE_PART_TWO_B;
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

  public static final char[][] COPY_GRID = Arrays.stream(GRID)
      .map(l -> {
        var emptyLine = new char[l.length];
        Arrays.fill(emptyLine, '.');
        return emptyLine;
      }).toArray(char[][]::new);

  public static final boolean[][] TOUCHED_GRID = Arrays.stream(GRID)
      .map(l -> new boolean[l.length]).toArray(boolean[][]::new);


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
  public static void printMaze(char[][] grid) {
    System.out.println(" [ Grid Start ]");
    for (char[] line : grid) {
      System.out.println(line);
    }
    System.out.println(" [ Grid End   ]");
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

    public final Function<Integer, Integer> xMod;
    public final Function<Integer, Integer> yMod;

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

      COPY_GRID[startY][startX] = GRID[startY][startX];
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
      COPY_GRID[start.y][start.x] = GRID[start.y][start.x];
      GRID[start.y][start.x] = '*';
      availableExit.moveToFrom(start);
      distanceCounter[0]++;
      return true;
    }

  }

  public enum FullDir {
    N(Dir.NORTH.xMod, Dir.NORTH.yMod),
    E(Dir.EAST.xMod, Dir.EAST.yMod),
    S(Dir.SOUTH.xMod, Dir.SOUTH.yMod),
    W(Dir.WEST.xMod, Dir.WEST.yMod),
    NE(Dir.NORTH.xMod.compose(Dir.EAST.xMod), Dir.NORTH.yMod.compose(Dir.EAST.yMod)),
    NW(Dir.NORTH.xMod.compose(Dir.WEST.xMod), Dir.NORTH.yMod.compose(Dir.WEST.yMod)),
    SE(Dir.SOUTH.xMod.compose(Dir.EAST.xMod), Dir.SOUTH.yMod.compose(Dir.EAST.yMod)),
    SW(Dir.SOUTH.xMod.compose(Dir.WEST.xMod), Dir.SOUTH.yMod.compose(Dir.WEST.yMod));

    private final Function<Integer, Integer> xMod;
    private final Function<Integer, Integer> yMod;

    FullDir(Function<Integer, Integer> xMod, Function<Integer, Integer> yMod) {
      this.xMod = xMod;
      this.yMod = yMod;
    }

    public Optional<Character> characterInThisDirectionFrom(int x, int y, char[][] grid) {
      final Integer newY = this.yMod.apply(y);
      final Integer newX = this.xMod.apply(x);

      if (newY < 0 || newY >= grid.length) {
        return Optional.empty();
      }
      if (newX < 0 || newX >= grid[y].length) {
        return Optional.empty();
      }
      return Optional.of(grid[newY][newX]);
    }

  }


//  sealed interface Floodable {
//    int x(); int y();
//
//    boolean isPipe();
//
//  }

  public record XY(int x, int y) {}
//  public record XY(int x, int y) implements Floodable {}


  public static void floodFill(char[][] grid) {
    Queue<XY> nextFloodable = new LinkedList<>();

    // Fill the queue with border cells that are not part of the loop
    for (int x = 0; x < grid[0].length; x++) {
      if (grid[0][x] == '.') nextFloodable.offer(new XY(x, 0));
      if (grid[grid.length - 1][x] == '.') nextFloodable.offer(new XY(x, grid.length - 1));
    }

    for (int y = 0; y < grid.length; y++) {
      if (grid[y][0] == '.') nextFloodable.offer(new XY(0, y));
      if (grid[y][grid[y].length - 1] == '.') nextFloodable.offer(new XY(grid[y].length - 1, y));
    }

    while (!nextFloodable.isEmpty()) {
      var flood = nextFloodable.poll();
      if (grid[flood.y][flood.x] == 'X' || grid[flood.y][flood.x] != '.') continue;
      grid[flood.y][flood.x] = 'X';
      for (FullDir dir : FullDir.values()) {
        var dirX = dir.xMod.apply(flood.x);
        var dirY = dir.yMod.apply(flood.y);
        if (dir.characterInThisDirectionFrom(flood.x, flood.y, grid).isPresent()) {
          if (grid[dirY][dirX] == '.') {
            nextFloodable.offer(new XY(dirX, dirY));
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    printMaze(GRID);

    final Coord start = findStart();
    final PuzzleSolver solver = new PuzzleSolver(start.x, start.y);
    var longestDistance = solver.findLongestDistanceFromStart();

    printMaze(COPY_GRID);

    System.out.println(longestDistance);

    char[][] boom = new char[3 * COPY_GRID.length][];
    for (int y3 = 0; y3 < COPY_GRID.length; y3++) {
      boom[3 * y3] = new char[3 * COPY_GRID[y3].length];
      boom[(3 * y3) + 1] = new char[3 * COPY_GRID[y3].length];
      boom[(3 * y3) + 2] = new char[3 * COPY_GRID[y3].length];
    }

    for (int y = 0; y < COPY_GRID.length; y++) {
      for (int x = 0; x < COPY_GRID[y].length; x++) {

        // Resize the original grid...

        var x3 = 3 * x;
        var y3 = 3 * y;

        //║
        //═
        //╚
        //╝
        //╗
        //╔

        if (COPY_GRID[y][x] == '.') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '.';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '.';
          boom[y3 + 1][x3 + 1] = '.';
          boom[y3 + 1][x3 + 2] = '.';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '.';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '║') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '║';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '.';
          boom[y3 + 1][x3 + 1] = '║';
          boom[y3 + 1][x3 + 2] = '.';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '║';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '═') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '.';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '═';
          boom[y3 + 1][x3 + 1] = '═';
          boom[y3 + 1][x3 + 2] = '═';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '.';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '╚') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '║';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '.';
          boom[y3 + 1][x3 + 1] = '╚';
          boom[y3 + 1][x3 + 2] = '═';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '.';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '╝') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '║';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '═';
          boom[y3 + 1][x3 + 1] = '╝';
          boom[y3 + 1][x3 + 2] = '.';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '.';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '╗') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '.';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '═';
          boom[y3 + 1][x3 + 1] = '╗';
          boom[y3 + 1][x3 + 2] = '.';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '║';
          boom[y3 + 2][x3 + 2] = '.';
        }
        else if (COPY_GRID[y][x] == '╔') {
          boom[y3][x3] = '.';
          boom[y3][x3 + 1] = '.';
          boom[y3][x3 + 2] = '.';
          boom[y3 + 1][x3] = '.';
          boom[y3 + 1][x3 + 1] = '╔';
          boom[y3 + 1][x3 + 2] = '═';
          boom[y3 + 2][x3] = '.';
          boom[y3 + 2][x3 + 1] = '║';
          boom[y3 + 2][x3 + 2] = '.';
        }


      }
    }

    printMaze(boom);

    floodFill(boom);

    printMaze(boom);

    char[][] scaledDown = new char[COPY_GRID.length][];
    for (int y = 0; y < COPY_GRID.length; y++) {
      scaledDown[y] = new char[COPY_GRID[y].length];
    }

    for (int y = 0; y < scaledDown.length; y++) {
      for (int x = 0; x < scaledDown[y].length; x++) {
        scaledDown[y][x] = boom[(3*y) + 1][(3*x) + 1];
      }
    }

    printMaze(scaledDown);

    var enclosedTiles = 0;
    for (int y = 0; y < scaledDown.length; y++) {
      for (int x = 0; x < scaledDown[y].length; x++) {
        if (scaledDown[y][x] == '.') enclosedTiles++;
//        if (GRID[y][x] != '*' && GRID[y][x] != 'X') enclosedTiles++;
      }
    }

    System.out.println(enclosedTiles);
  }

}
