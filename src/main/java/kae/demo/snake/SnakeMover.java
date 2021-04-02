package kae.demo.snake;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.jcip.annotations.GuardedBy;

public class SnakeMover {

  private final Object lock = new Object();

  private final Game game;

  private final BlockingQueue<Direction> moves = new ArrayBlockingQueue<>(3);
  private Direction previousMove = Direction.RIGHT;

  private final Thread gameThread = new Thread(this::gameLoop);

  @GuardedBy("lock")
  private boolean stopped = true;

  private final AtomicInteger speed = new AtomicInteger(500);

  public SnakeMover(Game game) {
    this.game = game;
    game.addListener(
        new GameListener() {
          @Override
          public void moved() {}

          @Override
          public void ate() {
            speed.updateAndGet(v -> (int) (v * 0.9));
          }

          @Override
          public void gameOver() {
            stop();
          }
        });
  }

  private void gameLoop() {
    while (true) {
      try {
        synchronized (lock) {
          if (stopped || game.state().equals(GameState.GAME_OVER)) {
            break;
          }
        }

        moveSnake();
      } catch (InterruptedException ignored) {
      }
    }
  }

  private void moveSnake() throws InterruptedException {
    Direction direction = moves.poll(speed.get(), TimeUnit.MILLISECONDS);
    if (direction == null) {
      direction = previousMove;
    } else if (previousMove.opposite().equals(direction)) {
      return;
    }
    game.move(direction);
    previousMove = direction;
  }

  public void start() {
    synchronized (lock) {
      if (!stopped) {
        throw new IllegalStateException("Already started.");
      }
      stopped = false;
    }
    gameThread.start();
  }

  public void stop() {
    synchronized (lock) {
      if (stopped) {
        return;
      }
      stopped = true;
    }
    gameThread.interrupt();
  }

  public void enqueue(Direction direction) {
    synchronized (lock) {
      if (stopped) {
        return;
      }
    }

    moves.offer(direction);
  }
}
