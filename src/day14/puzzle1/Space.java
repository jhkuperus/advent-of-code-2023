package day14.puzzle1;

public interface Space {

  static Space parse(int x, int y, char c) {
    return switch (c) {
      case '#' -> new Blocked(x, y);
      case '.' -> new Empty(x, y, new RockHolder());
      case 'O' -> new Empty(x, y, new RockHolder(new RollingRocks.Rock(x, y)));
      default -> throw new IllegalArgumentException("Illegal character detected: " + c);
    };
  }

  int x();
  int y();
  default boolean canMoveInto() { return false; }
  default void setRock(RollingRocks.Rock rock) { throw new IllegalArgumentException("Cannot set a Rock on a blocked space"); }
  default RollingRocks.Rock getRock() { return null; };


  class RockHolder {
    RockHolder() {}
    RockHolder(RollingRocks.Rock rock) { this.rock = rock; }
    public RollingRocks.Rock rock;
  }

  record Empty(int x, int y, RockHolder holder) implements Space {
    @Override
    public boolean canMoveInto() {
      return holder.rock == null;
    }

    @Override
    public void setRock(RollingRocks.Rock rock) {
      holder.rock = rock;
    }

    @Override
    public RollingRocks.Rock getRock() {
      return holder.rock;
    }
  }
  record Blocked(int x, int y) implements Space {
  }


}
