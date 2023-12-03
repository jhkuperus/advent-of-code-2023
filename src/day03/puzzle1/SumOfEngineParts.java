package day03.puzzle1;

import java.util.Arrays;

public class SumOfEngineParts {

  public static void main(String[] args) {
    System.out.println(Arrays.stream(EngineSchematic.engineParts)
        .filter(ep -> ep instanceof EnginePart.EngineNumber)
        .filter(ep -> ((EnginePart.EngineNumber) ep).adjacentSymbols().length > 0)
        .mapToInt(ep -> ((EnginePart.EngineNumber) ep).number())
        .sum());
  }
}
