package day12.puzzle2;

import day12.puzzle1.DamagedSprings;
import day12.puzzle1.DamagedSprings.Group;
import day12.puzzle1.Input;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DynamicSprings {

//  public static final String INPUT = "???????#???? 1,1,2,1";
  public static final String INPUT = Input.PUZZLE;

  public static final DamagedSprings.Line[] lines = INPUT.lines()
      .map(DamagedSprings.Line::parse)
      .toArray(DamagedSprings.Line[]::new);

  public static final Map<CE, CR> cache = new ConcurrentHashMap<>();

  public record CE(String group, int[] sizes) {
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CE ce = (CE) o;

      if (!group.equals(ce.group)) return false;
      return Arrays.equals(sizes, ce.sizes);
    }

    @Override
    public int hashCode() {
      int result = group.hashCode();
      result = 31 * result + Arrays.hashCode(sizes);
      return result;
    }

    @Override
    public String toString() {
      return "CE{" +
             "group='" + group + '\'' +
             ", sizes=" + Arrays.toString(sizes) +
             '}';
    }
  }

  public record CR(BigInteger variations, int[] sizes, String[] vars) {
    @Override
    public String toString() {
      return "CR{" +
             "variations=" + variations +
             ", sizes=" + Arrays.toString(sizes) +
             ", vars=" + Arrays.toString(vars) +
             '}';
    }
  }



//  public static CR[] determineGroupVariations(Group g, int[] remainingGroupSizes) {
//    var numberOfTilesUsed = 0;
//    List<CR> results = new ArrayList<>();
//
//    for (int i = 0; i < remainingGroupSizes.length; i++) {
//      var nextGroupSize = remainingGroupSizes[i];
//
//      if (numberOfTilesUsed + nextGroupSize > g.springs().length()) {
//        break;
//      }
//
//      var nextGroupsToTry = Arrays.copyOf(remainingGroupSizes, i + 1);
//      var nextJob = new CE(g.springs(), nextGroupsToTry);
//      if (!cache.containsKey(nextJob)) {
//        var localResult = variate(nextJob);
//        cache.put(nextJob, localResult);
//      }
//      results.add(cache.get(nextJob));
//
//      numberOfTilesUsed += nextGroupSize + 1;
//    }
//
//    return results.toArray(CR[]::new);
//  }

  public static BigInteger determineFullVariations(Group[] gs, int[] groupSizes) {
    if (groupSizes.length == 0) {
      final boolean noMoreKnowns = Arrays.stream(gs).allMatch(Predicate.not(Group::hasKnowns));

      return noMoreKnowns ? BigInteger.ONE : BigInteger.ZERO;
    } else if (gs.length == 0) {
      // We have groups left to assign, but no groups to work with, invalid combination
      return BigInteger.ZERO;
    }

    var result = BigInteger.ZERO;

    var currentGroup = gs[0];
    // First see what happens when we do nothing with the group
    if (!currentGroup.hasKnowns()) {
      result = result.add(determineFullVariations(Arrays.stream(gs).skip(1).toArray(Group[]::new), groupSizes));
    }

    // Now start assigning groups and recurse to see if that works
    var numberOfTilesUsed = 0;
    List<CR> results = new ArrayList<>();
    final var groupsAfterThisOne = Arrays.stream(gs).skip(1).toArray(Group[]::new);

    for (int i = 0; i < groupSizes.length; i++) {
      var nextGroupSize = groupSizes[i];

      if (numberOfTilesUsed + nextGroupSize > currentGroup.springs().length()) {
        break;
      }

      var nextGroupsToTry = Arrays.copyOf(groupSizes, i + 1);
      var nextJob = new CE(currentGroup.springs(), nextGroupsToTry);
      if (!cache.containsKey(nextJob)) {
        var localResult = variate(nextJob);
        cache.put(nextJob, localResult);
      }
      final CR groupVariations = cache.get(nextJob);
//      System.out.println(Arrays.toString(groupVariations.vars));
      if (groupVariations.variations.compareTo(BigInteger.ZERO) > 0) {

        var recursedVariations = determineFullVariations(groupsAfterThisOne, Arrays.stream(groupSizes).skip(i + 1).toArray());

        if (!recursedVariations.equals(BigInteger.ZERO)) {
          result = result.add(recursedVariations.multiply(groupVariations.variations));
        }
      }

      numberOfTilesUsed += nextGroupSize + 1;
    }

    return result;
  }

  /*

  var(??????? 1,1)
  #. + var(????? 1)
  #. + #. + var(??? 0)
  #. + .#. + var(?? 0)
  #. + ..#. + var(? 0)
  #. + ...#.
  #. + ....#
  .#. + var(???? 1)
  .#. + #. + var(?? 0)
  .#. + .#. + var(? 0)
  .#. + ..#.
  .#. + ...#




   */

  private static CR variate(CE input) {
    if (input.sizes.length == 0) {
      // All groups were fitted

      var isInvalid = input.group.indexOf('#') >= 0;
      return new CR(isInvalid ? BigInteger.ZERO : BigInteger.ONE, input.sizes, null);
//      return new CR(isInvalid ? 0 : 1, input.sizes, isInvalid ? new String[] {""} : new String[] { input.group });
    } else if (input.group.isEmpty()) {
      return new CR(BigInteger.ZERO, input.sizes, null);
//      return new CR(0, input.sizes, new String[] {""});
    }

//    var variations = new ArrayList<String>();
    var result = BigInteger.ZERO;
    // Try to fit the next group
    for (int x = 0; x < input.group.length(); x++) {
      var nextGroupSize = input.sizes[0];

      // If previous index is '#', we will always end up with a group that's too large, so we can stop here
      var previousIndex = x - 1;
      if (previousIndex >= 0 && input.group.charAt(previousIndex) == '#') {
        return new CR(result, input.sizes, null);
//        return new CR(result, input.sizes, new String[]{""});
      }

      // Additional requirement: the next character must be a '?' or a '.' or there must not be a next character
      var nextIndex = x + nextGroupSize;
      var groupFits = (x + nextGroupSize <= input.group.length())
                  && ((nextIndex < input.group.length() && input.group.charAt(nextIndex) != '#') || nextIndex >= input.group.length())
                  && ((previousIndex >= 0 && input.group.charAt(previousIndex) != '#') || previousIndex < 0);

      if (groupFits) {
        // If the group fits, try to fit the remaining group after it
        final CE nextJob = new CE(input.group.substring(Math.min(nextIndex + 1, input.group.length() )), Arrays.stream(input.sizes).skip(1).toArray());
        if (!cache.containsKey(nextJob)) {
          var localResult = variate(nextJob);
          cache.put(nextJob, localResult);
        }
        final CR tmp = cache.get(nextJob);
        final var xX = x;
//        variations.addAll(Arrays.stream(tmp.vars).map(s -> {
//          final char[] charArray = input.group.substring(0, Math.min(nextIndex + 1, input.group.length())).toCharArray();
//          for (int i = xX; i < (xX + nextGroupSize); i++) charArray[i] = '#';
//          return new String(charArray) + s;
//        }).collect(Collectors.toList()));
        result = result.add(tmp.variations);
      } else {
        // The group doesn't fit, let the loop continue
      }
    }

    return new CR(result, input.sizes, null);
//    return new CR(result, input.sizes, variations.toArray(String[]::new));
  }

  /*

  1?1????
  1??1???
  1???1??
  1????1?
  1?????1
  ?1?1???
  ?1??1??
  ?1???1?
  ?1????1
  ??1?1??
  ??1??1?
  ??1???1
  ???1?1?
  ???1??1
  ????1?1

  1?1????
  1??1???
  1???1??
  1????1?
  1?????1
  ?1?1???
  ?1??1??
  ?1???1?
  ?1????1

   */

  public static final String[] originalLines = INPUT.lines().toArray(String[]::new);

  public static void main(String[] args) {
    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < lines.length; i++) {
//      System.out.println(lines[i]);
      final BigInteger bigInteger = determineFullVariations(lines[i].groups(), lines[i].groupSizes());
      result = result.add(bigInteger);
//      System.out.println(bigInteger);
      System.out.println(STR."\{originalLines[i]} \{bigInteger} ");
    }
    System.out.println(result);
  }

}
