package day16.puzzle1;

import day10.puzzle1.PipeMaze;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Beams {

  public static final String INPUT = Input.PUZZLE;

  record BeamHeadPosition(Room room, PipeMaze.Dir enteringFrom) {}

  record Room(int x, int y, char content, boolean[] beams, boolean[] energized) {

    @Override
    public String toString() {
      return switch (content) {
        case '|' -> "|";
        case '-' -> "-";
        case '\\' -> "\\";
        case '/' -> "/";
        case '.' ->
          (!energized[0]) ? "." : (
              (beams[0] ^ beams[1] ^ beams[2] ^ beams[3]) ? (beams[0] ? "v" : beams[1] ? "<" : beams[2] ? "^" : ">") :
                  "" + ((beams[0] ? 1 : 0) + (beams[1] ? 1 : 0) + (beams[2] ? 1 : 0) + (beams[3] ? 1 : 0))
          );
        default -> throw new IllegalArgumentException();
      };
    }

    List<BeamHeadPosition> acceptBeamFrom(PipeMaze.Dir enteringFrom) {
      energized[0] = true;

      // A beam that already passed through, we don't need to try again
      if (beams[enteringFrom.ordinal()]) {
        return List.of();
      }

      beams[enteringFrom.ordinal()] = true;

      if (content == '.' || (content == '|' && (enteringFrom == PipeMaze.Dir.NORTH || enteringFrom == PipeMaze.Dir.SOUTH))
            || (content == '-' && (enteringFrom == PipeMaze.Dir.EAST || enteringFrom == PipeMaze.Dir.WEST))) {

        return newBeamHeadFor(x, y, enteringFrom.inverse());
      }

      if (content == '/') {
        PipeMaze.Dir exitingToDir = switch(enteringFrom) {
          case NORTH -> PipeMaze.Dir.WEST;
          case EAST -> PipeMaze.Dir.SOUTH;
          case SOUTH -> PipeMaze.Dir.EAST;
          case WEST -> PipeMaze.Dir.NORTH;
        };

        return newBeamHeadFor(x, y, exitingToDir);
      }

      if (content == '\\') {
        PipeMaze.Dir exitingToDir = switch(enteringFrom) {
          case NORTH -> PipeMaze.Dir.EAST;
          case EAST -> PipeMaze.Dir.NORTH;
          case SOUTH -> PipeMaze.Dir.WEST;
          case WEST -> PipeMaze.Dir.SOUTH;
        };

        return newBeamHeadFor(x, y, exitingToDir);
      }

      if (content == '-') {
        return Stream.of(
            PipeMaze.Dir.EAST, PipeMaze.Dir.WEST
        )
            .flatMap(d -> newBeamHeadFor(x, y, d).stream())
            .collect(Collectors.toList());
      }

      if (content == '|') {
        return Stream.of(
            PipeMaze.Dir.NORTH, PipeMaze.Dir.SOUTH
        )
            .flatMap(d -> newBeamHeadFor(x, y, d).stream())
            .collect(Collectors.toList());
      }

      return List.of();
    }

  }

  private static List<BeamHeadPosition> newBeamHeadFor(int x, int y, PipeMaze.Dir exitingToDir) {
    final Integer newX = exitingToDir.xMod.apply(x);
    final Integer newY = exitingToDir.yMod.apply(y);
    return (newX >= 0 && newX < rooms[0].length) && (newY >= 0 && newY < rooms.length)
        ? List.of(new BeamHeadPosition(rooms[newY][newX], exitingToDir.inverse()))
        : List.of();
  }

  public static Room[][] parseRooms(String input) {
    final char[][] chars = input.lines().map(String::toCharArray).toArray(char[][]::new);
    Room[][] result = new Room[chars.length][chars[0].length];

    for (int y = 0; y < chars.length; y++) {
      for (int x = 0; x < chars[y].length; x++) {
        result[y][x] = new Room(x, y, chars[y][x], new boolean[4], new boolean[1]);
      }
    }

    return result;
  }

  public static final Room[][] rooms = parseRooms(INPUT);




  public static int analyzeBeamPath(BeamHeadPosition start) {
    Queue<BeamHeadPosition> workQueue = new LinkedList<>();
    workQueue.add(start);

    while (!workQueue.isEmpty()) {
      var currentBeamHead = workQueue.poll();
      List<BeamHeadPosition> nextHeads = currentBeamHead.room.acceptBeamFrom(currentBeamHead.enteringFrom);

      nextHeads.stream().forEach(workQueue::offer);
    }

    var energizedRooms = 0;
    for (int y = 0; y < rooms.length; y++) {
      for (int x = 0; x < rooms.length; x++) {
        if (rooms[y][x].energized[0]) energizedRooms++;
      }
    }

    return energizedRooms;
  }

  public static void resetRooms() {
    for (Room[] room : rooms) {
      for (Room room1 : room) {
        room1.energized[0] = false;
        room1.beams[0] = false;
        room1.beams[1] = false;
        room1.beams[2] = false;
        room1.beams[3] = false;
      }
    }
  }

  public static List<BeamHeadPosition> generateStartingPositions() {
    var result = new ArrayList<BeamHeadPosition>();

    for (int y = 0; y < rooms.length; y++ ) {
      result.add(new BeamHeadPosition(rooms[y][0], PipeMaze.Dir.WEST));
      result.add(new BeamHeadPosition(rooms[y][rooms[0].length - 1], PipeMaze.Dir.EAST));
    }

    for (int x = 0; x < rooms[0].length; x++ ) {
      result.add(new BeamHeadPosition(rooms[0][x], PipeMaze.Dir.NORTH));
      result.add(new BeamHeadPosition(rooms[rooms.length - 1][x], PipeMaze.Dir.SOUTH));
    }

    return result;
  }

  public static void main(String[] args) {
    List<BeamHeadPosition> startingPositions = generateStartingPositions();

    int highestEnergized = 0;
    BeamHeadPosition highestStartingPos = null;
    for (BeamHeadPosition startingPosition : startingPositions) {
        resetRooms();
        int energized = analyzeBeamPath(startingPosition);

        if (energized > highestEnergized) {
          highestEnergized = energized;
          highestStartingPos = startingPosition;
        }
    }

    System.out.println(highestEnergized);

    resetRooms();
    analyzeBeamPath(highestStartingPos);

    for (Room[] y : rooms) {
      for (Room room : y) {
        System.out.print(room);
      }
      System.out.println();
    }
  }


}
