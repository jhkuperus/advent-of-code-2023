package day05.puzzle1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static day05.puzzle1.Input.PUZZLE_INPUT;

public class ParsedInput {

  public static final String[] lines = PUZZLE_INPUT.lines().toArray(String[]::new);

  // Seeds Puzzle 1
  public static final LongStream seeds = Arrays.stream(lines[0].split(":")[1].trim().split(" "))
      .filter(Predicate.not(String::isBlank))
      .mapToLong(Long::parseLong);


  public static final ArrayList<ArrayList<String>> mapBlocks = Arrays.stream(lines)
      .filter(Predicate.not(l -> l.startsWith("seeds:")))
      .dropWhile(String::isBlank)
      .collect(() -> {
            var result = new ArrayList<ArrayList<String>>();
            result.add(new ArrayList<String>());
            return result;
          }, (acc, nextLine) -> {
            if (nextLine.isBlank()) {
              acc.add(new ArrayList<>());
            } else {
              acc.getLast().add(nextLine);
            }
          }, (a, b) -> {
            throw new RuntimeException("This is going length end badly... rewrite it");
          }
      );

  public static Map<String, AlmanakStuff.AlmanakMap> mappings = mapBlocks.stream()
      .map(AlmanakStuff.AlmanakMap::parse)
      .collect(Collectors.toMap(AlmanakStuff.AlmanakMap::from, Function.identity()));


}
