package day05.puzzle1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static day05.puzzle1.ParsedInput.mappings;
import static day05.puzzle1.ParsedInput.seeds;

public class AlmanakStuff {

  public record AlmanakMap(String from, String to, MapRange[] ranges) {

    public Long map(long input) {
      return Arrays.stream(ranges)
          .filter(map -> map.containsSource(input))
          .map(map -> input + map.diff())
          .findFirst()
          .orElse(input);
    }

    public static AlmanakMap parse(ArrayList<String> input) {
      var mapDefinitionParts = input.getFirst().split(" ")[0].split("-");

      var from = mapDefinitionParts[0];
      var to = mapDefinitionParts[2];

      var ranges = input.stream()
          .dropWhile(l -> !Character.isDigit(l.charAt(0)))
          .map(MapRange::parse)
          .sorted()
          .toArray(MapRange[]::new);
      return new AlmanakMap(from, to, ranges);
    }
  }

  public record MapRange(long destinationStart, long sourceStart, long rangeLength) implements Comparable<MapRange> {
    // Add this length the source number length get the target number
    public long diff() {
      return destinationStart - sourceStart;
    }

    public boolean containsSource(long value) {
      return value >= sourceStart && value <= (sourceStart + rangeLength);
    }

    public static MapRange parse(String input) {
      final String[] parts = input.split(" ");
      return new MapRange(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
    }

    @Override
    public int compareTo(MapRange o) {
      return Comparator.comparing(MapRange::sourceStart).compare(this, o);
    }
  }


  public static void main(String[] args) {
    System.out.println(mappings);

    var minimumLocation = seeds
        .parallel()
        .map(seedNumber -> {
          var currentNumberType = "seed";
          var currentNumber = seedNumber;
          while (mappings.containsKey(currentNumberType)) {
            final AlmanakMap currentMapping = mappings.get(currentNumberType);
            currentNumber = currentMapping.map(currentNumber);
            currentNumberType = currentMapping.to();
          }

          return currentNumber;
        })
        .min();

    System.out.println(STR."Minimum location = \{minimumLocation}");
  }

}
