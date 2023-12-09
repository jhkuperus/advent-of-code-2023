package day09.puzzle1;

import java.awt.image.SampleModel;
import java.util.Arrays;

public class OasisExtrapolator {

  public static final long[][] sequences = Input.PUZZLE_INPUT.lines()
      .map(l -> Arrays.stream(l.split(" ")).mapToLong(Long::valueOf).toArray())
      .toArray(long[][]::new);

  public static void main(String[] args) {
    long[] results = new long[sequences.length];

    for (int i = 0; i < sequences.length; i++) {
       results[i] = analyseAndExtrapolateSequence(sequences[i]);
    }

    System.out.println(Arrays.stream(results).sum());
  }

  public static long analyseAndExtrapolateSequence(long[] sequence) {
    var lastNumbers = new long[sequence.length - 1];
    var depth = 0;

    var workingSequence = new long[sequence.length];
    var currentSequenceLength = sequence.length;

    System.arraycopy(sequence, 0, workingSequence, 0, sequence.length);

    var sequenceSum = 0L;
    do {
      lastNumbers[depth++] = workingSequence[currentSequenceLength - 1];

      sequenceSum = calculateDiffSequence(workingSequence, currentSequenceLength--);
    } while (sequenceSum != 0 && currentSequenceLength > 0);

    var result = 0L;
    // Switch to adding numbers again
    for (int i = depth - 1; i >= 0; i--) {
      result += lastNumbers[i];
    }

    return result;
  }

  public static long calculateDiffSequence(long[] sequence, int currentLength) {
    var sum = 0L;

    for (int i = 0; i < currentLength - 1; i++) {
      sequence[i] = sequence[i + 1] - sequence[i];
      sum += sequence[i];
    }

    return sum;
  }



}
