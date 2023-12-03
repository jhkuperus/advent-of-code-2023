package day02.puzzle1;

import java.util.Map;
import java.util.function.BiFunction;

public record CubeSet(int blueCubes, int redCubes, int greenCubes) {

  public CubeSet withRed(int newRedCubes) {
    return new CubeSet(blueCubes, newRedCubes, greenCubes);
  }

  public CubeSet withBlue(int newBlueCubes) {
    return new CubeSet(newBlueCubes, redCubes, greenCubes);
  }

  public CubeSet withGreen(int newGreenCubes) {
    return new CubeSet(blueCubes, redCubes, newGreenCubes);
  }

  public int power() {
    return redCubes * greenCubes * blueCubes;
  }

  public static CubeSet empty() {
    return new CubeSet(0, 0, 0);
  }

  private static final Map<String, BiFunction<CubeSet, Integer, CubeSet>> colorSetters = Map.of(
      "red", CubeSet::withRed,
      "blue", CubeSet::withBlue,
      "green", CubeSet::withGreen
  );

  public static CubeSet parse(String input) {
    var result = empty();
    var colorSets = input.split(",");
    for (int i = 0; i < colorSets.length; i++) {
      var setData = colorSets[i].trim().split(" ");
      var numberOfCubes = Integer.parseInt(setData[0]);
      var color = setData[1];

      result = colorSetters.get(color.trim()).apply(result, numberOfCubes);
    }

    return result;
  }
}
