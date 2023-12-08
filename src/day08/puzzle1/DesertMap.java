package day08.puzzle1;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DesertMap {

  public static final String start = "AAA";
  public static final String stop = "ZZZ";

  public record Room(String id, String left, String right) {

    public String nextFromInstruction(char instr) {
      return switch (instr) {
        case 'L' -> left;
        case 'R' -> right;
        default -> throw new IllegalArgumentException(STR."Wrong instruction: \{instr}");
      };
    }

    public static Room parse(String input) {
      var roomIdAndConnections = input.split("=");
      roomIdAndConnections[1] = roomIdAndConnections[1].trim();
      var leftAndRight = roomIdAndConnections[1].substring(1, roomIdAndConnections[1].length() - 1).split(",");

      return new Room(roomIdAndConnections[0].trim(), leftAndRight[0].trim(), leftAndRight[1].trim());
    }
  }

  public record Instructions(String instr) {}

  public static final Instructions theInstructions = Input.PUZZLE_INPUT.lines()
      .findFirst()
      .map(Instructions::new)
      .get();

  public static final List<Room> theRooms = Input.PUZZLE_INPUT.lines()
      .skip(2)
      .map(Room::parse)
      .collect(Collectors.toList());

  public static Map<String, Room> theMap = theRooms.stream()
      .collect(Collectors.toMap(
          Room::id,
          Function.identity()
      ));

  public static void main(String[] args) {
    System.out.println(theRooms);
    final String result = instructionsToSolveTheMap();
    System.out.println(result);
    System.out.println(result.length());
  }


  public static final String instructionsToSolveTheMap() {
    final StringBuilder result = new StringBuilder();

    var currentPosition = start;
    var positionInInstructions = 0;

    while (!currentPosition.equals(stop)) {
      var nextInstruction = theInstructions.instr.charAt(positionInInstructions);
      var nextPosition = theMap.get(currentPosition).nextFromInstruction(nextInstruction);
      result.append(nextInstruction);

      currentPosition = nextPosition;
      positionInInstructions = (positionInInstructions + 1) % theInstructions.instr.length();
    }

    return result.toString();
  }

}
