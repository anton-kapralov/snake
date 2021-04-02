package kae.demo.snake;

import static java.util.Collections.unmodifiableList;

import java.util.List;

public class GameSnapshot {

  private final List<int[]> snake;
  private final List<int[]> foods;

  public GameSnapshot(List<int[]> snake, List<int[]> foods) {
    this.snake = unmodifiableList(snake);
    this.foods = unmodifiableList(foods);
  }

  public List<int[]> snake() {
    return snake;
  }

  public List<int[]> foods() {
    return foods;
  }
}
