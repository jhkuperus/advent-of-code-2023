package day02.puzzle2;

import day02.puzzle1.CubeSet;
import day02.puzzle1.Game;
import day02.puzzle1.GameFilter;

import java.util.Arrays;

public class GameMinimalCubes {

  public static void main(String[] args) {
    final int summedPowers = Arrays.stream(GameFilter.GAMES)
        .map(Game::highestRevealedNumbers)
        .mapToInt(CubeSet::power)
        .sum();

    System.out.println(summedPowers);
  }
}
