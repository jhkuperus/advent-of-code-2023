package day02.puzzle1;

import java.util.Arrays;

import static day02.puzzle1.Input.PUZZLE_INPUT;

public class GameFilter {

  public static Game[] GAMES = PUZZLE_INPUT.lines()
//      .parallel()
      .map(Game::parse)
      .toArray(Game[]::new);

  public static void main(String[] args) {
    final var redCubes = 12;
    final var greenCubes = 13;
    final var blueCubes = 14;
    final var availableCubes = new CubeSet(blueCubes, redCubes, greenCubes);

    var sumOfGameIds = Arrays.stream(GAMES)
        .filter(game -> game.isGamePossibleWithAvailableCubes(availableCubes))
        .mapToInt(game -> game.id())
        .sum();

    System.out.println(sumOfGameIds);
  }


}
