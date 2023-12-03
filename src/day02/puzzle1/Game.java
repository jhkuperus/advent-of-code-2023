package day02.puzzle1;

import java.util.Arrays;

public record Game(int id, CubeSet[] revealedSets, CubeSet highestRevealedNumbers) {

  Game(int id, CubeSet[] revealedSets) {
    this(id, revealedSets, Arrays.stream(revealedSets)
        .reduce(CubeSet.empty(), (cs1, cs2) -> new CubeSet(
            Math.max(cs1.blueCubes(), cs2.blueCubes()),
            Math.max(cs1.redCubes(), cs2.redCubes()),
            Math.max(cs1.greenCubes(), cs2.greenCubes())
        )));
  }

  public boolean isGamePossibleWithAvailableCubes(CubeSet availableCubes) {
    return highestRevealedNumbers.blueCubes() <= availableCubes.blueCubes() &&
           highestRevealedNumbers.redCubes() <= availableCubes.redCubes() &&
           highestRevealedNumbers.greenCubes() <= availableCubes.greenCubes();
  }

  public static Game parse(String input) {
    var gameAndRevealedSets = input.split(":");
    var gameId = Integer.parseInt(gameAndRevealedSets[0].trim().split(" ")[1]);
    System.out.println("Parsing game " + gameId);
    var rawRevealedSets = gameAndRevealedSets[1].split(";");
    var revealedSets = Arrays.stream(rawRevealedSets).map(CubeSet::parse).toArray(CubeSet[]::new);

    return new Game(gameId, revealedSets);
  }

}
