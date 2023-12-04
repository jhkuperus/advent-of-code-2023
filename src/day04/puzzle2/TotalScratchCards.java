package day04.puzzle2;

import day04.puzzle1.Card;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static day04.puzzle1.ScratchCardInput.cards;

public class TotalScratchCards {

  public static void main(String[] args) {
    for (int idx = 0; idx < cards.length; idx++) {
      var numberOfWinningNumbers = cards[idx].getWinningNumbersYouHave().size();
      for (int copyIndex = 0; copyIndex < numberOfWinningNumbers  && (copyIndex + idx + 1) < cards.length; copyIndex++) {
        cards[idx + copyIndex + 1].copies().addAndGet(cards[idx].copies().get());
      }
    }

    System.out.println(Arrays.stream(cards)
        .map(Card::copies)
        .mapToInt(AtomicInteger::get)
        .sum());
  }

}
