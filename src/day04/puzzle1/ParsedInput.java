package day04.puzzle1;

import java.util.Arrays;
import java.util.function.Predicate;

import static day04.puzzle1.Input.PUZZLE_INPUT;

public class ParsedInput {

  public static Card[] cards = PUZZLE_INPUT.lines()
      .filter(Predicate.not(String::isBlank))
      .map(Card::parse)
      .toArray(Card[]::new);

  public static void main(String[] args) {
    System.out.println(Arrays.toString(cards));
  }


}
