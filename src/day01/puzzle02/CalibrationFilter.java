package day01.puzzle02;


import day01.puzzle01.CalibrationDocument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CalibrationFilter {

  public static void main(String[] args) {

    System.out.println(CalibrationDocument.VALUE.lines()
        .parallel()
        .filter(Predicate.not(String::isBlank))
        .mapToInt(line -> {

          var firstDigit = findDigitFromIndex(line, 0, 1);
          var lastDigit = findDigitFromIndex(line, line.length() - 1, -1);

          return Integer.parseInt(STR."\{firstDigit}\{lastDigit}");
        })
        .sum());

  }

  static int findDigitFromIndex(String line, int startIndex, int direction) {
    var detector = direction > 0 ? forwardDetector : reverseDetector;
    var activeMatchAttempts = new DigitDetector.DetectorState[0];
    for (int i = startIndex; direction > 0 ? (i < line.length()) : (i >= 0); i += direction) {

      activeMatchAttempts = detector.accept(activeMatchAttempts, line.charAt(i));

      if (activeMatchAttempts.length == 1 && activeMatchAttempts[0].digitFound()) {
        return activeMatchAttempts[0].resultingDigit;
      }

    }

    throw new IllegalArgumentException(STR."No digit found!?: \{line} - \{startIndex} \{direction}");
  }

  static class DigitDetector {

    private final Edge[] edges;
    private final int start;
    private final int stop;
    private final boolean reversed;

    private final Map<Character, int[]>[] machine;

    DigitDetector(Edge[] edges, int start, int stop, boolean reversed) {
      this.edges = edges;
      this.start = start;
      this.stop = stop;
      this.reversed = reversed;

      this.machine = new Map[Math.max(start, stop) + 1];
      for (int i = 0; i < this.machine.length; i++) {
        this.machine[i] = new HashMap<>();
      }
      Arrays.stream(this.edges)
          .forEach(edge -> {
            final Map<Character, int[]> characterMap = this.machine[edge.from];
            if (characterMap.containsKey(edge.c)) {
              var oldTargets = characterMap.get(edge.c);
              var newTargets = Arrays.copyOf(oldTargets, oldTargets.length + 1);
              newTargets[newTargets.length - 1] = edge.to;
              characterMap.put(edge.c, newTargets);
            } else {
              characterMap.put(edge.c, new int[] { edge.to });
            }
          });
    }

    DetectorState freshState() {
      return new DetectorState();
    }

    DetectorState[] accept(DetectorState[] currentMatchAttempts, char c) {
      if (Character.isDigit(c)) {
        final DetectorState finalState = freshState();
        finalState.finishWithLiteralDigit(c);
        return new DetectorState[] {finalState};
      }

      // Always copy the array and add an additional Fresh state to allow any character to start a match
      currentMatchAttempts = Arrays.copyOf(currentMatchAttempts, currentMatchAttempts.length + 1);
      currentMatchAttempts[currentMatchAttempts.length - 1] = freshState();

      for (int matchIndex = 0; matchIndex < currentMatchAttempts.length; matchIndex++) {
        var currentMatchAttempt = currentMatchAttempts[matchIndex];

        var nextStates = new int[0];
        for (int stateIndex = 0; stateIndex < currentMatchAttempt.currentMachineState.length; stateIndex++) {
          var eligibleTransitions = this.machine[currentMatchAttempt.currentMachineState[stateIndex]];

          if (eligibleTransitions.containsKey(c)) {
            var nextIndexInNextStates = nextStates.length;
            var additionalStates = eligibleTransitions.get(c);
            nextStates = Arrays.copyOf(nextStates, nextStates.length + additionalStates.length);
            System.arraycopy(additionalStates, 0, nextStates, nextIndexInNextStates, additionalStates.length);
          }
        }

        if (nextStates.length > 0) {
          // Collect the character and continue through the machine
          currentMatchAttempt.acceptCharacterAndProceedToState(c, nextStates);

          // Check if we have reached the final state, if so, return it as the only state
          if (currentMatchAttempt.currentMachineState.length == 1 && currentMatchAttempt.currentMachineState[0] == stop) {
            currentMatchAttempt.finishWithBuffer();
            return new DetectorState[] { currentMatchAttempt };
          }
        } else {
          // Current character could not continue the match, but perhaps it can start a new match
          currentMatchAttempt.fail();
        }
      }

      return Arrays.stream(currentMatchAttempts).filter(Predicate.not(DetectorState::hasFailed)).toArray(DetectorState[]::new);
    }

    class DetectorState {

      private int[] currentMachineState;
      private char[] buffer = new char[5];
      private int nextBufferIndex = 0;
      private int resultingDigit = 0;

      private boolean failed = false;

      DetectorState() {
        this.currentMachineState = new int[] {DigitDetector.this.start};
      }

      void acceptCharacterAndProceedToState(char c, int[] newState) {
        this.buffer[nextBufferIndex++] = c;
        this.currentMachineState = newState;
      }

      void finishWithLiteralDigit(char digit) {
        this.resultingDigit = Integer.parseInt("" + digit);
        this.currentMachineState = new int[] {DigitDetector.this.stop};
      }

      void finishWithBuffer() {
        StringBuilder result = new StringBuilder();
        result.append(buffer, 0, nextBufferIndex);
        if (DigitDetector.this.reversed) {
          result.reverse();
        }

        resultingDigit = switch (result.toString()) {
          case "one" -> 1;
          case "two" -> 2;
          case "three" -> 3;
          case "four" -> 4;
          case "five" -> 5;
          case "six" -> 6;
          case "seven" -> 7;
          case "eight" -> 8;
          case "nine" -> 9;
          default -> throw new IllegalArgumentException("Unknown digit: " + result.toString());
        };
      }

      boolean digitFound() {
        return this.currentMachineState.length == 1 && this.currentMachineState[0] == DigitDetector.this.stop;
      }

      void fail() {
        this.failed = true;
      }

      boolean hasFailed() {
        return this.failed;
      }
    }

  }

  record Edge(int from, char c, int to) {
    Edge invert() {
      return new Edge(to, c, from);
    }
  }

  static final Edge[] edges = new Edge[] {
    new Edge(0, 'o', 1),
    new Edge(1, 'n', 2),
    new Edge(2, 'e', 25),

    new Edge(0, 't', 3),
    new Edge(3, 'w', 4),
    new Edge(4, 'o', 25),

    new Edge(3, 'h', 5),
    new Edge(5, 'r', 6),
    new Edge(6, 'e', 7),
    new Edge(7, 'e', 25),

    new Edge(0, 'f', 8),
    new Edge(8, 'o', 9),
    new Edge(9, 'u', 10),
    new Edge(10, 'r', 25),

    new Edge(8, 'i', 11),
    new Edge(11, 'v', 12),
    new Edge(12, 'e', 25),

    new Edge(0, 's', 13),
    new Edge(13, 'i', 14),
    new Edge(14, 'x', 25),

    new Edge(13, 'e', 15),
    new Edge(15, 'v', 16),
    new Edge(16, 'e', 17),
    new Edge(17, 'n', 25),

    new Edge(0, 'e', 18),
    new Edge(18, 'i', 19),
    new Edge(19, 'g', 20),
    new Edge(20, 'h', 21),
    new Edge(21, 't', 25),

    new Edge(0, 'n', 22),
    new Edge(22, 'i', 23),
    new Edge(23, 'n', 24),
    new Edge(24, 'e', 25)
  };

  static final Edge[] reverseEdges = Arrays.stream(edges).map(Edge::invert).toArray(Edge[]::new);

  static final DigitDetector forwardDetector = new DigitDetector(edges, 0, 25, false);
  static final DigitDetector reverseDetector = new DigitDetector(reverseEdges, 25, 0, true);


}
