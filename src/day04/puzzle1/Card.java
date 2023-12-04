package day04.puzzle1;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record Card(int cardNumber, Set<Integer> winningNumbers, Set<Integer> numbersYouHave, AtomicInteger copies) {

  public Card(int cardNumber, Set<Integer> winningNumbers, Set<Integer> numbersYouHave) {
    this(cardNumber, winningNumbers, numbersYouHave, new AtomicInteger(1));
  }

  public Set<Integer> getWinningNumbersYouHave() {
    return numbersYouHave.stream()
        .filter(winningNumbers::contains)
        .collect(Collectors.toSet());
  }

  public static Card parse(String input) {
    var cardAndNumbers = input.split(":");
    int cardNumber = 0;

    final String[] parts = cardAndNumbers[0].trim().split(" ");
    cardNumber = Integer.parseInt(parts[parts.length - 1]);

    var winningAndHavingNumbers = cardAndNumbers[1].split("\\|");

    return new Card(cardNumber, parseNumberList(winningAndHavingNumbers[0]), parseNumberList(winningAndHavingNumbers[1]));
  }

  private static Set<Integer> parseNumberList(String numbers) {
    return Arrays.stream(numbers.trim().split(" "))
        .filter(Predicate.not(String::isBlank))
        .map(Integer::parseInt)
        .collect(Collectors.toSet());
  }
}
