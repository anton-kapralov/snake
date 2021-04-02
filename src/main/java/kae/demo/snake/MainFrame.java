package kae.demo.snake;

import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

  private final GameController gameController;
  private final int cellSize;

  public MainFrame(GameController gameController) throws HeadlessException {
    super("Snake");
    this.gameController = gameController;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    cellSize = gd.getDisplayMode().getWidth() / 64;

    int width = cellSize * gameController.width() + 10;
    int height = cellSize * (gameController.height() + 1) + 10;

    setSize(width, height);
    setResizable(false);

    setLocationRelativeTo(null);

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowOpened(WindowEvent e) {
            onNewGame();
          }

          @Override
          public void windowClosing(WindowEvent e) {
            onEndGame();
          }
        });

    addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            onKeyPressed(e, gameController);
          }
        });
  }

  private void onKeyPressed(KeyEvent e, GameController gameController) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_D:
        if (e.isMetaDown()) {
          onNewGame();
        }
        break;
      case KeyEvent.VK_UP:
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_DOWN:
      case KeyEvent.VK_LEFT:
        gameController.keyPressed(e);
        break;
    }
  }

  private void onNewGame() {
    gameController.endGame();
    Game game = gameController.newGame();
    Container contentPane = getContentPane();
    contentPane.removeAll();
    contentPane.add(new GamePanel(game, cellSize));
    contentPane.revalidate();
    contentPane.repaint();
  }

  private void onEndGame() {
    gameController.endGame();
  }
}
