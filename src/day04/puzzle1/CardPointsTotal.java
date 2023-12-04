package day04.puzzle1;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class CardPointsTotal {
  public static void main(String[] args) {
    System.out.println(Arrays.stream(ScratchCardInput.cards)
        .map(Card::getWinningNumbersYouHave)
        .filter(Predicate.not(Set::isEmpty))
        .mapToInt(s -> 1 << (s.size() - 1))
        .sum());
  }
}
