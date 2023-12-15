package day15.puzzle2;

import day15.puzzle1.Input;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static day15.puzzle1.HASH.hashIt;

public class Buckets {

  public static final String INPUT = Input.PUZZLE;

  static class Box {

    Slot firstSlot, lastSlot;

    Slot addLens(int focalStrength) {
      if (firstSlot == null) {
        firstSlot = lastSlot = new Slot();
      } else {
        lastSlot.next = new Slot();
        lastSlot.next.previous = lastSlot;
        lastSlot = lastSlot.next;
      }
      lastSlot.focalStrength = focalStrength;

      return lastSlot;
    }

    void removeLensInSlot(Slot s) {
      if (s == firstSlot) {
        firstSlot = s.next;
        if (firstSlot != null)
          firstSlot.previous = null;
        else
          lastSlot = null;
      } else if (s == lastSlot) {
        lastSlot = lastSlot.previous;
        if (lastSlot != null)
          lastSlot.next = null;
        else
          firstSlot = null;
      } else {
        var currentSlot = firstSlot.next;

        while (currentSlot != s) {
          currentSlot = currentSlot.next;
        }

        if (currentSlot.previous != null)
          currentSlot.previous.next = currentSlot.next;
        if (currentSlot.next != null)
          currentSlot.next.previous = currentSlot.previous;
      }
    }

    class Slot {
      int focalStrength;
      Slot next, previous;

    }

    public String toString() {
      var builder = new StringBuilder();
      for (var slot = firstSlot; slot.next != null; slot = slot.next) {
        builder.append(STR."[\{slot.focalStrength}]");
      }
      return builder.toString();
    }
  }

  public static final Box[] boxes = IntStream.range(0, 256).mapToObj(c -> new Box()).toArray(Box[]::new);
  public static final Map<LensCoord, Box.Slot> labelSlots = new HashMap<>();

  record LensCoord(String label, int boxNr) {}

  public static void main(String[] args) {
    final String[] parts = INPUT.split(",");

    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < parts.length; i++) {
      final String trimmed = parts[i].trim();

      var indexOfDash = trimmed.indexOf('-');
      var indexOfEquals = trimmed.indexOf('=');

      if (indexOfEquals > 0) {
        var label = trimmed.substring(0, indexOfEquals);
        int focalStrength = Integer.parseInt(trimmed.substring(indexOfEquals + 1));
        int boxNr = hashIt(label);
        var coord = new LensCoord(label, boxNr);

        if (labelSlots.containsKey(coord)) {
          labelSlots.get(coord).focalStrength = focalStrength;
        } else {
          final Box.Slot slot = boxes[boxNr].addLens(focalStrength);
          labelSlots.put(coord, slot);
        }
      } else if (indexOfDash > 0) {
        var label = trimmed.substring(0, indexOfDash);
        int boxNr = hashIt(label);
        var coord = new LensCoord(label, boxNr);

        if (labelSlots.containsKey(coord)) {
          final Box.Slot slot = labelSlots.get(coord);
          labelSlots.remove(coord);

          boxes[boxNr].removeLensInSlot(slot);
        }
      }
    }

    for (int boxNr = 0; boxNr < boxes.length; boxNr++) {
      if (boxes[boxNr].firstSlot == null) continue;

      var lensSlot = 1;
      var currentSlot = boxes[boxNr].firstSlot;
      do {
        result = result.add(BigInteger.valueOf(boxNr + 1).multiply(BigInteger.valueOf(lensSlot)).multiply(BigInteger.valueOf(currentSlot.focalStrength)));

        currentSlot = currentSlot.next;
        lensSlot++;
      } while (currentSlot != null);

    }

    System.out.println(result);
  }


}
