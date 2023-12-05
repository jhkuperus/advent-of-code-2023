package day01.puzzle1;


import java.util.function.Predicate;

public class CalibrationFilter {

  public static void main(String[] args) {

    System.out.println(Input.VALUE.lines()
        .parallel()
        .filter(Predicate.not(String::isBlank))
        .mapToInt(line -> {

          var firstDigit = findDigitFromIndex(line, 0, 1);
          var lastDigit = findDigitFromIndex(line, line.length() - 1, -1);

          return Integer.parseInt(STR."\{firstDigit}\{lastDigit}");
        })
        .sum());

  }

  static char findDigitFromIndex(String line, int startIndex, int direction) {
    for (int i = startIndex; direction > 0 ? (i < line.length()) : (i >= 0); i += direction) {
      if (Character.isDigit(line.charAt(i))) {
        return line.charAt(i);
      }
    }

    throw new IllegalArgumentException(STR."No digit found!?: \{line} - \{startIndex} \{direction}");
  }

}
