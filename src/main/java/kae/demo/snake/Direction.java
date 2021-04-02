package kae.demo.snake;

public enum Direction {
  UP {
    @Override
    public Direction opposite() {
      return DOWN;
    }
  },
  RIGHT {
    @Override
    public Direction opposite() {
      return LEFT;
    }
  },
  DOWN {
    @Override
    public Direction opposite() {
      return UP;
    }
  },
  LEFT {
    @Override
    public Direction opposite() {
      return RIGHT;
    }
  };

  public abstract Direction opposite();
}
