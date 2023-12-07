package day07.puzzle2;

import day07.puzzle1.Input;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class CamelCardsWithJokers {

  @FunctionalInterface
  interface HandTypeDetector {
    boolean matchesType(int[] groups, int numberOfJokers);
  }

  // These things definitely have redundant situations, but I care not to optimize those 😇
  public static enum HandType {
    FIVE_OF_A_KIND((g, j) -> g.length > 0 && (g[0] == 5 || g[0] + j == 5) || j == 5),
    FOUR_OF_A_KIND((g, j) -> g.length > 0 && (g[0] == 4 || g[0] + j == 4)),
    FULL_HOUSE((g, j) -> (g.length > 1 && ((g[0] == 3 && g[1] == 2) || (g[0] + j == 3 && g[1] == 2) || (g[0] == 3 && g[1] + j == 2)))
        || (g.length == 1 && ((g[0] == 2 && j == 3) || (g[0] == 3 && j == 2)))
    ),
    THREE_OF_A_KIND((g, j) -> g.length > 0 && (g[0] == 3 || g[0] + j == 3)),
    TWO_PAIR((g, j) -> (g.length > 1 && ((g[0] == 2 && g[1] == 2) || (g[0] + j == 2 && g[1] == 2) || (g[0] == 2 && g[1] + j == 2)))
        || (g.length == 1 && ((g[0] == 2 && j == 2)))
    ),
    ONE_PAIR((g, j) -> (g.length > 1 && (g[0] == 2 || g[0] + j == 2)) || j == 2),
    HIGH_CARD((g, j) -> true);

    private final HandTypeDetector matcher;

    HandType(HandTypeDetector matcher) {
      this.matcher = matcher;
    }

    public boolean matchesGrouping(int[] groups, int numberOfJokers) {
      return this.matcher.matchesType(groups, numberOfJokers);
    }
  }

  public record Hand(String cards, int[] cardValues, int[] groups, int jokerCount, int bid, HandType type) implements Comparable<Hand> {


    public static Hand parse(String input) {
      final String[] parts = input.split(" ");

      Map<Character, Integer> cards = new HashMap<>();

      parts[0].chars().forEach(ch -> cards.merge((char) ch, 1, (x, y) -> x + y));
      var jokers = cards.containsKey('J') ? cards.get('J') : 0;
      cards.remove('J');

      final int[] groups = cards.values().stream()
          .mapToInt(Integer::valueOf)
          .boxed()
          .sorted(Comparator.reverseOrder())
          .mapToInt(Integer::valueOf)
          .toArray();

      final Optional<HandType> handType = Arrays.stream(HandType.values())
          .filter(ht -> ht.matchesGrouping(groups, jokers))
          .findFirst();

      final int[] cardValues = parts[0].chars()
          .map(ch -> switch ((char) ch) {
            case 'A' -> 14;
            case 'K' -> 13;
            case 'Q' -> 12;
            case 'T' -> 10;
            case '9' -> 9;
            case '8' -> 8;
            case '7' -> 7;
            case '6' -> 6;
            case '5' -> 5;
            case '4' -> 4;
            case '3' -> 3;
            case '2' -> 2;
            case 'J' -> 1;
            default -> throw new RuntimeException("IllegalCharacter: " + ((char)ch));
          })
          .toArray();

      return new Hand(parts[0], cardValues, groups, jokers, Integer.parseInt(parts[1]), handType.orElseGet(() -> HandType.HIGH_CARD));
    }

    private static int compareCardValuesTo(Hand o1, Hand o2) {
      return Arrays.compare(o2.cardValues(), o1.cardValues());
    }

    @Override
    public int compareTo(Hand o) {
      return (Comparator.comparing(Hand::type)
          .thenComparing(Hand::compareCardValuesTo)).reversed().compare(this, o);
    }
  }

  public static final Hand[] hands = Input.PUZZLE_INPUT.lines()
      .map(Hand::parse)
      .toArray(Hand[]::new);


  public static void main(String[] args) {
    Arrays.sort(hands);
    Arrays.stream(hands)
        .filter(h -> h.groups.length == 0)
        .forEach(System.out::println);

    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < hands.length; i++) {
      result = result.add(BigInteger.valueOf(hands[i].bid() * (i + 1)));
    }

    System.out.println(result);
  }

}
