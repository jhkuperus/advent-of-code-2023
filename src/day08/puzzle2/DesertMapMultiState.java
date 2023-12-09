package day08.puzzle2;

import day08.puzzle1.DesertMap;
import day08.puzzle1.Input;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static day08.puzzle1.DesertMap.*;

public class DesertMapMultiState {

  public static final long findCycleLength(DesertMap.Room from) {
    var steps = 0L;

    var currentPosition = from;
    var positionInInstructions = 0;

    while (!currentPosition.isEndingRoom()) {
      var nextInstruction = theInstructions.instr().charAt(positionInInstructions);
      var nextPosition = theMap.get(theMap.get(currentPosition.id()).nextFromInstruction(nextInstruction));

      steps++;

      currentPosition = nextPosition;
      positionInInstructions = (positionInInstructions + 1) % theInstructions.instr().length();
    }

    return steps;
  }


  public static void main(String[] args) {
    final Optional<BigInteger> reduce = theRooms.stream()
        .filter(Room::isStartingRoom)
        .map(DesertMapMultiState::findCycleLength)
        .map(BigInteger::valueOf)
        .reduce((l1, l2) -> {
          var product = l1.multiply(l2);
          var gcd = l1.gcd(l2);
          return product.divide(gcd);
        });

    System.out.println(reduce);
  }

}
