package day03.puzzle2;

import day03.puzzle1.ParsedInput;

public class SumOfGearRatios {
  public static void main(String[] args) {
    System.out.println(ParsedInput.SYMBOLS.values().stream()
        .filter(symbol -> symbol.adjacentNumbers().size() == 2)
        .mapToInt(symbol -> symbol.adjacentNumbers().stream().mapToInt(n -> n.number()).reduce(1, (x, y) -> x * y))
        .sum());
  }
}
