package day05.puzzle2;

import day05.puzzle1.Almanak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static day05.puzzle1.Almanak.lines;
import static day05.puzzle1.Almanak.mappings;

public class SeedRanges {

  public static final long[] seedRangeNumbers = Arrays.stream(lines[0].split(":")[1].trim().split(" "))
      .filter(Predicate.not(String::isBlank))
      .mapToLong(Long::parseLong)
      .toArray();

  record Range(String type, long from, long length) {
    public LongStream stream() {
      return LongStream.range(from, from + length);
    }
  }

  public static final Range[] seedRanges = IntStream.range(0, seedRangeNumbers.length / 2)
      .mapToObj(seedStartIndex -> new Range("seed", seedRangeNumbers[seedStartIndex * 2], seedRangeNumbers[(seedStartIndex * 2) + 1]))
      .toArray(Range[]::new);


  public static void main(String[] args) {
    var minimumLocation = Arrays.stream(seedRanges)
        .flatMap(seedRange -> {
          ArrayList<Range> currentRanges = new ArrayList<>();
          currentRanges.add(seedRange);

          while (mappings.containsKey(currentRanges.getFirst().type)) {
            var nextStage = new ArrayList<Range>();
            final Almanak.AlmanakMap almanakMap = mappings.get(currentRanges.getFirst().type);

            for (Range currentRange : currentRanges) {
              nextStage.addAll(mapRange(currentRange, almanakMap));
            }

            currentRanges = nextStage;
          }

          return currentRanges.stream()
              .map(Range::from);
        })
        .mapToLong(Long::valueOf)
        .min();

    System.out.println(minimumLocation);
  }

  public static ArrayList<Range> mapRange(Range source, Almanak.AlmanakMap mapping) {
    var results = new ArrayList<Range>();
    var remainder = source;

    System.out.println(STR."Mapping \{source}\nUsing:\n\{mapping}");

    for (int i = 0; i < mapping.ranges().length && remainder.length() > 0; i++) {
      var currentMapping = mapping.ranges()[i];
      System.out.println(STR." -- Considering \{mapping.ranges()[i]}");
      System.out.println(STR."     [ \{remainder.from()} < \{currentMapping.sourceStart()} = \{remainder.from() < currentMapping.sourceStart()}");
      // Check if there's a part _before_ the mapping range
      if (remainder.from() < currentMapping.sourceStart()) {
        // This segment is not mapped, so numbers stay the same, but we move to the next mapping type
        final Range splitOffRange = new Range(mapping.to(), remainder.from(), currentMapping.sourceStart() - remainder.from());
        remainder = new Range(remainder.type(), remainder.from() + splitOffRange.length(), remainder.length() - splitOffRange.length());
        results.add(splitOffRange);

        System.out.println(STR." -+ Split off prefix: \{splitOffRange}");
        System.out.println(STR." -+ Remainder       : \{remainder}");
      }

      System.out.println(STR."     [ \{remainder.from()} < \{currentMapping.sourceStart() + currentMapping.rangeLength()} = \{remainder.from() < (currentMapping.sourceStart() + currentMapping.rangeLength())} == \{currentMapping.containsSource(remainder.from())}");
      if (currentMapping.containsSource(remainder.from())) {
        // The remainder is (at least partially) inside the mapping range
        final var mappingOffset = remainder.from() - currentMapping.sourceStart();
        final var mappingLength = Math.min(remainder.length(), currentMapping.rangeLength() - mappingOffset);
        final Range splitOffMappedRange = new Range(mapping.to(), currentMapping.destinationStart() + mappingOffset, mappingLength);

        remainder = new Range(remainder.type(), remainder.from() + mappingLength, remainder.length() - mappingLength);

        System.out.println(STR." -+ Split off part  : \{splitOffMappedRange}");
        System.out.println(STR." -+ Remainder       : \{remainder}");

        results.add(splitOffMappedRange);
      }
    }

    if (remainder.length() > 0) {
      results.add(new Range(mapping.to(), remainder.from(), remainder.length()));
    }

    return results;
  }


}
