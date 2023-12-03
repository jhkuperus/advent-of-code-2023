package day03.puzzle1;

import java.util.Arrays;

public class SumOfEngineParts {

  public static void main(String[] args) {
    final int sum = Arrays.stream(EngineSchematic.PART_NUMBERS)
        .filter(ep -> ep instanceof EnginePart.EngineNumber)
        .filter(ep -> ((EnginePart.EngineNumber) ep).adjacentSymbols().length > 0)
        .mapToInt(ep -> ((EnginePart.EngineNumber) ep).number())
        .sum();

    System.out.println(STR."Result: \{sum} = \{sum == 525119 ? '✅' : '⛔'}");
  }
}
