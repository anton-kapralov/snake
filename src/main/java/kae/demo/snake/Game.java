package kae.demo.snake;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.jcip.annotations.GuardedBy;

public class Game {

  private final Object lock = new Object();

  private final List<GameListener> listeners = new CopyOnWriteArrayList<>();

  private final int height;
  private final int width;

  @GuardedBy("lock")
  private GameState state = GameState.PLAYING;

  @GuardedBy("lock")
  private final Deque<int[]> snake = new ArrayDeque<>();

  @GuardedBy("lock")
  private final Deque<int[]> foods = new ArrayDeque<>();

  private final Random random = new Random();

  public Game(int height, int width) {
    this.height = height;
    this.width = width;

    synchronized (lock) {
      snake.add(new int[] {0, 0});
      generateFood(5 + random.nextInt(10));
    }
  }

  private void generateFood(int n) {
    Set<Integer> snakePartSet = new HashSet<>(snake.size());
    for (int[] part : snake) {
      snakePartSet.add(part[0] * width + part[1]);
    }

    for (int i = 0; i < n; i++) {
      int r = random.nextInt(height);
      int c = random.nextInt(width);
      if (snakePartSet.contains(r * width + c)) {
        i--;
        continue;
      }
      foods.addLast(new int[] {r, c});
    }
  }

  public int height() {
    return height;
  }

  public int width() {
    return width;
  }

  public GameSnapshot getSnapshot() {
    synchronized (lock) {
      return new GameSnapshot(new ArrayList<>(snake), new ArrayList<>(foods));
    }
  }

  public GameState state() {
    synchronized (lock) {
      return state;
    }
  }

  public void move(Direction direction) {
    GameState newState;
    boolean ate = false;
    synchronized (lock) {
      int[] h = snake.getFirst();
      int r = h[0];
      int c = h[1];

      switch (direction) {
        case UP:
          r--;
          break;
        case RIGHT:
          c++;
          break;
        case DOWN:
          r++;
          break;
        case LEFT:
          c--;
          break;
      }

      if (r < 0 || r >= height || c < 0 || c >= width) {
        fail();
      } else if (tryEat(r, c)) {
        generateFood(1);
        ate = true;
      } else {
        moveSnake(r, c);
      }
      newState = state;
    }

    if (newState.equals(GameState.GAME_OVER)) {
      fireGameOver();
    } else if (ate) {
      fireAte();
    } else {
      fireMoved();
    }
  }

  private boolean tryEat(int r, int c) {
    for (Iterator<int[]> it = foods.iterator(); it.hasNext(); ) {
      int[] food = it.next();
      if (food[0] != r || food[1] != c) {
        continue;
      }

      it.remove();
      snake.addFirst(food);
      return true;
    }

    return false;
  }

  private void moveSnake(int r, int c) {
    snake.removeLast();

    checkSelfBite(r, c);

    if (!state.equals(GameState.GAME_OVER)) {
      snake.addFirst(new int[] {r, c});
    }
  }

  private void checkSelfBite(int r, int c) {
    for (int[] part : snake) {
      if (part[0] == r && part[1] == c) {
        fail();
        break;
      }
    }
  }

  private void fail() {
    state = GameState.GAME_OVER;
  }

  public void addListener(GameListener listener) {
    listeners.add(listener);
  }

  private void fireMoved() {
    for (GameListener listener : listeners) {
      listener.moved();
    }
  }

  private void fireAte() {
    for (GameListener listener : listeners) {
      listener.ate();
    }
  }

  private void fireGameOver() {
    for (GameListener listener : listeners) {
      listener.gameOver();
    }
  }
}
