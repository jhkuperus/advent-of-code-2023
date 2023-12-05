package day03.puzzle1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static day03.puzzle1.Input.PUZZLE_INPUT;

public class ParsedInput {

  public static String[] LINES = PUZZLE_INPUT.lines().toArray(String[]::new);

  record Position(int ln, int idx) {
    static Position of(int l, int i) { return new Position(l, i); }

    char charAt() {
      return LINES[ln].charAt(idx);
    }
  }

  public static Map<Position, EnginePart.EngineSymbol> SYMBOLS = new HashMap<>();

  private static Pattern regex = Pattern.compile("([0-9]+)");
  private record LineWithLineNumber(int lineNumber, String line) {}

  public static EnginePart[] PART_NUMBERS = IntStream.range(0, LINES.length)
      .mapToObj(lineNumber -> new LineWithLineNumber(lineNumber, LINES[lineNumber]))
      .flatMap(lineWithLineNumber -> {
        var partsInLine = new LinkedList<EnginePart>();
        final Matcher matcher = regex.matcher(lineWithLineNumber.line);
        while (matcher.find()) {
          final String matchedGroup = matcher.group();

          var adjacentSymbols = detectAdjacentSymbols(lineWithLineNumber.lineNumber, matcher.start(), matcher.end());
          final EnginePart.EngineNumber engineNumber = new EnginePart.EngineNumber(Integer.parseInt(matchedGroup), adjacentSymbols, lineWithLineNumber.lineNumber, matcher.start(), matcher.end());

          for (EnginePart.EngineSymbol adjacentSymbol : adjacentSymbols) {
            adjacentSymbol.adjacentNumbers().add(engineNumber);
          }

          partsInLine.add(engineNumber);
        }

        return partsInLine.stream();
      })
      .toArray(EnginePart[]::new);

  private static EnginePart.EngineSymbol[] detectAdjacentSymbols(final int lineNumber, final int start, final int end) {
    final var results = new ArrayList<Position>();
    final var firstLine = lineNumber == 0;
    final var lastLine = lineNumber == (LINES.length - 1);
    final var firstChar = start == 0;

    if (!firstLine) {
      if (!firstChar) results.add(Position.of(lineNumber - 1, start - 1));
      IntStream.range(start, end).forEach(cPos -> results.add(Position.of(lineNumber - 1, cPos)));
      if (end < LINES[lineNumber - 1].length()) results.add(Position.of(lineNumber - 1, end));
    }

    if (!lastLine) {
      if (!firstChar) results.add(Position.of(lineNumber + 1, start - 1));
      IntStream.range(start, end).forEach(cPos -> results.add(Position.of(lineNumber + 1, cPos)));
      if (end < LINES[lineNumber + 1].length()) results.add(Position.of(lineNumber + 1, end));
    }

    if (!firstChar) results.add(Position.of(lineNumber, start - 1));
    IntStream.range(start, end).forEach(cPos -> results.add(Position.of(lineNumber, cPos)));
    if (end < LINES[lineNumber].length()) results.add(Position.of(lineNumber, end));

    try {
      return results.stream()
          .filter(pos -> !Character.isDigit(pos.charAt()) && pos.charAt() != '.')
          .map(pos -> SYMBOLS.computeIfAbsent(pos, p -> new EnginePart.EngineSymbol("" + p.charAt(), p.ln, p.idx, p.idx + 1)))
          .toArray(EnginePart.EngineSymbol[]::new);
    } catch (Exception e) {
      System.out.println("!!!!!");
      System.out.println(LINES.length);
      System.out.println(STR."Parsing line: \{LINES[lineNumber]}");
      System.out.println(STR."Range: \{lineNumber} @ \{start}-\{end}");
      System.out.println(results);
      throw new RuntimeException(e);
    }

  }
}
