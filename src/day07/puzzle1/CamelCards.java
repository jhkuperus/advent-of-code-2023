package day07.puzzle1;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

public class CamelCards {

  public static enum HandType {
    FIVE_OF_A_KIND(g -> g.length > 0 && g[0] == 5),
    FOUR_OF_A_KIND(g -> g.length > 0 && g[0] == 4),
    FULL_HOUSE(g -> g.length > 1 && g[0] == 3 && g[1] == 2),
    THREE_OF_A_KIND(g -> g.length > 0 && g[0] == 3),
    TWO_PAIR(g -> g.length > 1 && g[0] == 2 && g[1] == 2),
    ONE_PAIR(g -> g.length > 1 && g[0] == 2),
    HIGH_CARD(g -> true);

    private final Predicate<int[]> matcher;

    HandType(Predicate<int[]> matcher) {
      this.matcher = matcher;
    }

    public boolean matchesGrouping(int[] groups) {
      return this.matcher.test(groups);
    }
  }

//  public static Comparator<String> cardComparator =

  public record Hand(String cards, int[] cardValues, int bid, HandType type) implements Comparable<Hand> {


    public static Hand parse(String input) {
      final String[] parts = input.split(" ");

      Map<Character, Integer> cards = new HashMap<>();

      parts[0].chars().forEach(ch -> cards.merge((char) ch, 1, (x, y) -> x + y));

      final int[] groups = cards.values().stream()
          .mapToInt(Integer::valueOf)
          .boxed()
          .sorted(Comparator.reverseOrder())
          .mapToInt(Integer::valueOf)
          .toArray();

      final Optional<HandType> handType = Arrays.stream(HandType.values()).filter(ht -> ht.matchesGrouping(groups)).findFirst();

      final int[] cardValues = parts[0].chars()
          .map(ch -> switch ((char) ch) {
            case 'A' -> 14;
            case 'K' -> 13;
            case 'Q' -> 12;
            case 'J' -> 11;
            case 'T' -> 10;
            case '9' -> 9;
            case '8' -> 8;
            case '7' -> 7;
            case '6' -> 6;
            case '5' -> 5;
            case '4' -> 4;
            case '3' -> 3;
            case '2' -> 2;
            default -> throw new RuntimeException("IllegalCharacter: " + ((char)ch));
          })
          .toArray();

      return new Hand(parts[0], cardValues, Integer.parseInt(parts[1]), handType.orElseGet(() -> HandType.HIGH_CARD));
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
    Arrays.stream(hands).forEach(System.out::println);

    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < hands.length; i++) {
      result = result.add(BigInteger.valueOf(hands[i].bid() * (i + 1)));
    }

    System.out.println(result);
  }

}
