package kae.demo.snake;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameBoardPanel extends JPanel {

  private static final Color SNAKE_COLOR = new Color(77, 116, 77);
  private static final Color FOOD_COLOR = new Color(219, 48, 48);

  private final Game game;
  private final int cellSize;

  public GameBoardPanel(Game game, int cellSize) {
    this.game = game;
    this.cellSize = cellSize;
    setBorder(BorderFactory.createEtchedBorder());
    setBackground(Color.WHITE);

    game.addListener(
        new GameListener() {
          @Override
          public void moved() {
            SwingUtilities.invokeLater(GameBoardPanel.this::onGameUpdated);
          }

          @Override
          public void ate() {
            SwingUtilities.invokeLater(GameBoardPanel.this::onGameUpdated);
          }

          @Override
          public void gameOver() {
            SwingUtilities.invokeLater(GameBoardPanel.this::onGameUpdated);
          }
        });
  }

  private void onGameUpdated() {
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.LIGHT_GRAY);

    int width = game.width() * cellSize;
    int height = game.height() * cellSize;

    for (int x = 0, i = 0; x < width; x += cellSize, i++) {
      g.drawString(String.valueOf(i), x + 2, cellSize - 2);
    }

    for (int y = 0, i = 0; y < height; y += cellSize, i++) {
      g.drawString(String.valueOf(i), 2, y + cellSize - 2);
    }

    for (int x = cellSize; x <= width; x += cellSize) {
      g.drawLine(x, 0, x, height);
    }

    for (int y = cellSize; y <= height; y += cellSize) {
      g.drawLine(0, y, width, y);
    }

    GameSnapshot snapshot = game.getSnapshot();
    for (int[] part : snapshot.snake()) {
      fillCell(g, part[0], part[1], SNAKE_COLOR);
    }
    for (int[] part : snapshot.foods()) {
      fillCell(g, part[0], part[1], FOOD_COLOR);
    }
  }

  private void fillCell(Graphics g, int row, int column, Color color) {
    g.setColor(color);
    int left = column * cellSize;
    int top = row * cellSize;
    g.fillRect(left, top, cellSize, cellSize);
  }
}
