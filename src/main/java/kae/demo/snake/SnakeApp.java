package kae.demo.snake;

import javax.swing.SwingUtilities;

public class SnakeApp {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> new MainFrame(new GameController(32, 32)).setVisible(true));
  }
}
