package kae.demo.snake;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GamePanel extends JPanel {

  public GamePanel(Game game, int cellSize) {
    setLayout(new BorderLayout());

    setBorder(new EmptyBorder(4, 4, 4, 4));
    add(new GameBoardPanel(game, cellSize), BorderLayout.CENTER);
  }
}
