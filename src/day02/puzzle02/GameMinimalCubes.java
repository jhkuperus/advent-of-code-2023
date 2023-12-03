package day02.puzzle02;

import day02.puzzle01.CubeSet;
import day02.puzzle01.Game;
import day02.puzzle01.GameList;

import java.util.Arrays;

public class GameMinimalCubes {

  public static void main(String[] args) {
    final int summedPowers = Arrays.stream(GameList.GAMES)
        .map(Game::highestRevealedNumbers)
        .mapToInt(CubeSet::power)
        .sum();

    System.out.println(summedPowers);
  }
}
