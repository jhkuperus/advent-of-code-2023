package day15.puzzle1;

import java.math.BigInteger;

public class HASH {
  public static final String INPUT = Input.PUZZLE;


  public static int hashIt(String input) {
    int val = 0;

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
//      System.out.println(STR."\{c} \{(int)c}");
      val += c;
      val *= 17;
      val %= 256;
    }

    return val;
  }

  public static void main(String[] args) {
    final String[] parts = INPUT.split(",");

    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < parts.length; i++) {
      var hash = hashIt(parts[i].trim());
      System.out.println(STR."\{parts[i]} = \{hash}");
      result = result.add(BigInteger.valueOf(hash));
    }

    System.out.println(result);
  }
}
