package day02.puzzle01;

import java.util.Arrays;
import java.util.stream.Stream;

public class GameFilter {

  public static void main(String[] args) {
    final var redCubes = 12;
    final var greenCubes = 13;
    final var blueCubes = 14;
    final var availableCubes = new CubeSet(blueCubes, redCubes, greenCubes);

    var sumOfGameIds = Arrays.stream(GameList.GAMES)
        .filter(game -> game.isGamePossibleWithAvailableCubes(availableCubes))
        .mapToInt(game -> game.id())
        .sum();

    System.out.println(sumOfGameIds);
  }


}
