package day03.puzzle1;

import java.util.LinkedList;
import java.util.List;

public sealed interface EnginePart {

  int lineNumber();
  int startIndex();
  int stopIndex();

  record EngineNumber(int number, EngineSymbol[] adjacentSymbols, int lineNumber, int startIndex, int stopIndex) implements EnginePart {

  }

  record EngineSymbol(String symbol, int lineNumber, int startIndex, int stopIndex, List<EngineNumber> adjacentNumbers) implements EnginePart {
    EngineSymbol(String symbol, int lineNumber, int startIndex, int stopIndex) {
      this(symbol, lineNumber, startIndex, stopIndex, new LinkedList<>());
    }

  }

}
