package day14.puzzle2;

import day14.puzzle1.RollingRocks;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static day10.puzzle1.PipeMaze.printMaze;
import static day14.puzzle1.RollingRocks.*;

public class SpingCycles {

  public static char[][] rotateGrid(char[][] grid) {
    var result = new char[grid[0].length][grid.length];

    for (int origY = 0; origY < grid.length; origY++) {
      var newX = result[0].length - origY - 1;
      for (int origX = 0; origX < grid[origY].length; origX++) {
        var newY = origX;

        result[newY][newX] = grid[origY][origX];
      }
    }

    return result;
  }

  record State(int hash, char[][] grid) {
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      State state = (State) o;

      return Arrays.deepEquals(grid, state.grid);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    static State of(char[][] grid) {
      return new State(
          Arrays.hashCode(Arrays.stream(grid).mapToInt(Arrays::hashCode).toArray()),
          Arrays.stream(grid).map(a -> Arrays.copyOf(a, a.length)).toArray(char[][]::new)
      );
    }
  }

  static Map<State, BigInteger> firstCycleOfState = new HashMap<>();
  static Map<BigInteger, State> stateOfCycleNr = new HashMap<>();

  public static void main(String[] args) {
    var nrOfCycles = BigInteger.ZERO;
    var target = new BigInteger("1000000000");

    while (nrOfCycles.compareTo(target) < 0) {
      for (int c = 0; c < 4; c++) {
//        System.out.println("Start...");
//        printMaze(grid);

        for (int x = 0; x < grid[0].length; x++) {
          rollEverythingNorthInCol(x);
        }

//        System.out.println("Rolled...");
//        printMaze(grid);
//        System.out.println("Rotating...");
        grid = rotateGrid(grid);
      }

      // Completed a cycle, let's see if we've seen it before
      nrOfCycles = nrOfCycles.add(BigInteger.ONE);
      var state = State.of(grid);

      if (firstCycleOfState.containsKey(state)) {
        // Found a cycle!
        var firstCycleOfThisState = firstCycleOfState.get(state);
        var cycleLength = nrOfCycles.subtract(firstCycleOfThisState);

        // Advance as far as we can
        while (nrOfCycles.add(cycleLength).compareTo(target) < 0) {
          nrOfCycles = nrOfCycles.add(cycleLength);
        }

        // Diff to target
        var remainingCycles = target.subtract(nrOfCycles);
        var finalStateEqualsStateAtCycle = firstCycleOfThisState.add(remainingCycles);

        var finalState = stateOfCycleNr.get(finalStateEqualsStateAtCycle);
        grid = finalState.grid;
        break;
      } else {
        firstCycleOfState.put(state, nrOfCycles);
        stateOfCycleNr.put(nrOfCycles, state);
      }
    }

    printMaze(grid);
    var result = BigInteger.ZERO;

    for (int x = 0; x < grid[0].length; x++) {
      result = result.add(countWeightInCol(x));
    }

    System.out.println(result);


  }



}
