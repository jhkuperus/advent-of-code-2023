package day12.puzzle1;

import java.util.Arrays;
import java.util.function.Predicate;

public class DamagedSprings {

  public record Group(String springs) {
    @Override
    public String toString() {
      return springs;
    }
  }

  public record Line(Group[] groups, char[] compact, int[] groupSizes, int totalMissingDamaged, int totalDamaged) {

    public int variations() {
      return variate(0, Arrays.copyOf(compact, compact.length), 0, 0);
    }

    private int variate(int accumulator, char[] work, int nextWorkIndex, int nextGroupIndex) {
      if (nextGroupIndex >= groupSizes.length) {
        // All groups were fitted

        // Final Check: if sum is higher than sum of groups, reject solution
        var nrOfBroken = 0;
        for (int i = 0; i < work.length; i++) {
          if (work[i] == '#')
            nrOfBroken++;
        }
//        System.out.println(work);

        return nrOfBroken == totalDamaged ? accumulator + 1 : accumulator;
      } else if (nextWorkIndex >= work.length) {
        return accumulator;
      }

      // Try to fit the next group
      for (int x = nextWorkIndex; x < work.length; x++) {
        if (work[x] == '.') continue;

        var nextGroupSize = groupSizes[nextGroupIndex];
        var groupFits = true;
        for (int gs = 0; gs < nextGroupSize && groupFits; gs++) {
          groupFits = (x + gs) < work.length && work[x + gs] != '.';
        }

        // Additional requirement: the next character must be a '?' or a '.' or there must not be a next character
        var nextIndex = x + nextGroupSize;
        var previousIndex = x - 1;
        groupFits = groupFits
                    && ((nextIndex < work.length && work[nextIndex] != '#') || nextIndex >= work.length)
                    && ((previousIndex >= 0 && work[previousIndex] != '#') || previousIndex < 0);

        if (groupFits) {
          // If the group fits, try to fit the remaining groups after it
          final char[] copy = Arrays.copyOf(work, work.length);
          for (int idx = x; idx < nextIndex; idx++) copy[idx] = '#';
          accumulator = variate(accumulator, copy, x + groupSizes[nextGroupIndex] + 1, nextGroupIndex + 1);
        } else {
          // The group doesn't fit, let the loop continue
        }
      }

      return accumulator;
    }

    public static Line parse(String input) {
      final String[] parts = input.split(" ");
//      parts[0] = parts[0] + "?" + parts[0] + "?" + parts[0] + "?" + parts[0] + "?" + parts[0];
//      parts[1] = parts[1] + "," + parts[1] + "," + parts[1] + "," + parts[1] + "," + parts[1];
      final int[] groupSizes = Arrays.stream(parts[1].split(","))
          .mapToInt(Integer::valueOf)
          .toArray();

      final String compact = parts[0].replaceAll("\\.+", ".");
      final Group[] groups = Arrays.stream(compact.split("\\."))
          .filter(Predicate.not(String::isBlank))
          .map(Group::new)
          .toArray(Group[]::new);

      final int expectedTotalDamaged = Arrays.stream(groupSizes).sum();
      final int actualDamaged = (int) parts[0].chars()
          .filter(c -> c == '#')
          .count();

      return new Line(groups, compact.toCharArray(), groupSizes, expectedTotalDamaged - actualDamaged, expectedTotalDamaged);
    }

    public String toString() {
      return STR."Line[compact=\{new String(compact)}, groups=\{Arrays.toString(groups)}, groupSizes=\{Arrays.toString(groupSizes)}, \{totalMissingDamaged}]";
    }
  }

  public static final String INPUT = Input.PUZZLE;

  public static final Line[] lines = INPUT.lines()
      .map(Line::parse)
      .toArray(Line[]::new);


  public static void main(String[] args) {
//    for (int i = 0; i < lines.length; i++) {
//      System.out.println(STR."\{new String(lines[i].compact)} - \{Arrays.toString(lines[i].groupSizes)} - \{lines[i].variations()}");
//      System.out.println(lines[i].variations());
//    }

    System.out.println(Arrays.stream(lines)
        .parallel()
        .mapToInt(Line::variations)
        .sum());
  }

}
