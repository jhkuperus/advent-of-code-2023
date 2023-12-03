package day03.puzzle1;

public sealed interface EnginePart {

  int lineNumber();
  int startIndex();
  int stopIndex();

  record EngineNumber(int number, String[] adjacentSymbols, int lineNumber, int startIndex, int stopIndex) implements EnginePart {

  }

  record EngineSymbol(String symbol, int lineNumber, int startIndex, int stopIndex) implements EnginePart {

  }

}
