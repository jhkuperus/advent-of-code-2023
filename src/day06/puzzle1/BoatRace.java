package day06.puzzle1;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.LongStream;

public class BoatRace {

  public record Race(long time, long distance) {

    public long winningButtonPresses() {
      return LongStream.range(0, time + 1)
          .map(pressTime -> (pressTime * time) - (pressTime * pressTime) - distance)
          .filter(result -> result > 0)
          .count();
    }

  }

  /*

    dist(x) = (time - x) * x
    dist(x) = x*time - x*x

    win(x) = x*time - x*x - record

   */

  public static void main(String[] args) {
    final Optional<Long> productOfWinningPossibilities = Arrays.stream(Input.PUZZLE_INPUT)
        .map(Race::winningButtonPresses)
        .reduce((a, b) -> a * b);

    System.out.println(productOfWinningPossibilities);
  }

}
