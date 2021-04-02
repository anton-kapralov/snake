package kae.demo.snake;

import java.awt.event.KeyEvent;
import net.jcip.annotations.GuardedBy;

public class GameController {

  private final Object lock = new Object();

  private final int height;
  private final int width;

  @GuardedBy("lock")
  private Game game;

  @GuardedBy("lock")
  private SnakeMover snakeMover;

  public GameController(int height, int width) {
    this.height = height;
    this.width = width;
  }

  public int height() {
    return height;
  }

  public int width() {
    return width;
  }

  public Game newGame() {
    synchronized (lock) {
      if (game != null) {
        throw new IllegalStateException("End previous game first!");
      }

      game = new Game(height, width);
      snakeMover = new SnakeMover(game);
      snakeMover.start();
    }
    return game;
  }

  public void endGame() {
    synchronized (lock) {
      if (game == null) {
        return;
      }

      game = null;
      snakeMover.stop();
      snakeMover = null;
    }
  }

  public void keyPressed(KeyEvent e) {
    synchronized (lock) {
      if (game == null) {
        return;
      }

      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          snakeMover.enqueue(Direction.UP);
          break;
        case KeyEvent.VK_RIGHT:
          snakeMover.enqueue(Direction.RIGHT);
          break;
        case KeyEvent.VK_DOWN:
          snakeMover.enqueue(Direction.DOWN);
          break;
        case KeyEvent.VK_LEFT:
          snakeMover.enqueue(Direction.LEFT);
          break;
      }
    }
  }
}
