package day09.puzzle2;

import java.util.Arrays;

import static day09.puzzle1.OasisExtrapolator.*;

public class OasisHistoryExtrapolator {

  public static void main(String[] args) {
    long[] results = new long[sequences.length];

    for (int i = 0; i < sequences.length; i++) {
      results[i] = analyseAndExtrapolateSequence(sequences[i]);
    }

    System.out.println(Arrays.stream(results).sum());
  }

  public static long analyseAndExtrapolateSequence(long[] sequence) {
    var firstNumbers = new long[sequence.length - 1];
    var depth = 0;

    var workingSequence = new long[sequence.length];
    var currentSequenceLength = sequence.length;

    System.arraycopy(sequence, 0, workingSequence, 0, sequence.length);

    var sequenceSum = 0L;
    do {
      firstNumbers[depth++] = workingSequence[0];

      sequenceSum = calculateDiffSequence(workingSequence, currentSequenceLength--);
    } while (sequenceSum != 0 && currentSequenceLength > 0);

    var result = 0L;
    // Switch to adding numbers again
    for (int i = depth - 1; i >= 0; i--) {
      result = firstNumbers[i] - result;
    }

    return result;
  }


}
