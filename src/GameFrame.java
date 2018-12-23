import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by Precastwig on 02/09/2017.
 */
public class GameFrame extends JPanel {
    private static final int DEFAULT_PADDING    = 10;
    private static final int DEFAULT_BORDER_WIDTH = 3;
    private static final int MINIMUM_CELL_LENGTH = 60;
    private static final Font DEFAULT_FONT = new Font("Times New Roman",
                                                    Font.BOLD,
                                                    50);
    private int padding;
    private int borderWidth;
    private Color backgroundColor;
    private Color borderColor;
    private Color majorborderColor;
    private Color foregroundColor;
    private Color highlightOccupiedBorderColor;
    private Color highlightOccupiedCellColor;
    private Color highlightFreeBorderColor;
    private Color highlightFreeCellColor;
    private Color playerOneColor;
    private Color playerTwoColor;
    private Color outOfPlayColor;

    private int highlightCellX;
    private int highlightCellY;

    private int previousHighlightCellX;
    private int previousHighlightCellY;

    private int lastValidCellX = -1;
    private int lastValidCellY = -1;

    private GameGrid currentGrid;
    private labelListener listener;
    private boolean end = false;
    private int currentplayer = 1;
    private JLabel bottomlabel;
    private static int OPACITY = 100;
    private static int HIGHLIGHTOPACITY = 170;

    public GameFrame(labelListener label) {
        this.padding = DEFAULT_PADDING;
        this.borderWidth = DEFAULT_BORDER_WIDTH;
        this.backgroundColor = Color.WHITE;
        this.borderColor = Color.lightGray;
        this.majorborderColor = Color.BLACK;
        this.foregroundColor = Color.BLACK;
        this.highlightFreeBorderColor = new Color(100,200,100,HIGHLIGHTOPACITY);
        this.highlightFreeCellColor = new Color(9,181,31,HIGHLIGHTOPACITY);
        this.highlightOccupiedBorderColor = new Color(244,66,182,HIGHLIGHTOPACITY);
        this.highlightOccupiedCellColor = new Color(232,39,39,HIGHLIGHTOPACITY);
        this.playerOneColor = new Color(66,134,244,OPACITY);
        this.playerTwoColor = new Color(244,155,66,OPACITY);
        this.outOfPlayColor = new Color(181,172,164,75);
        setFont(DEFAULT_FONT);

        bottomlabel = new JLabel("");
        currentGrid = new GameGrid();
        listener = label;
        this.setMinimumSize(new Dimension(500,500));
        CanvasMouseListener mouselisten = new CanvasMouseListener();
        this.addMouseListener(mouselisten);
        this.addMouseMotionListener(mouselisten);
        CanvasKeyListener keylisten = new CanvasKeyListener();
        this.addKeyListener(keylisten);
        this.setFocusable(true);
        this.requestFocus();
    }

    @Override
    public void update(Graphics g) {
        //some starting maths
        int availableWidth  = getWidth() - 2 * padding;
        int availableHeight = getHeight() - 2 * padding;

        int horizontalCells = currentGrid.getWidth();
        int verticalCells   = currentGrid.getHeight();

        int cellWidth  = (availableWidth - (horizontalCells + 1) * borderWidth)
                / horizontalCells;

        int cellHeight = (availableHeight - (verticalCells + 1) * borderWidth)
                / verticalCells;

        int cellLength = Math.min(cellWidth, cellHeight);

        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        int occupiedWidth = (horizontalCells * (cellLength + borderWidth)) +
                borderWidth;
        int occupiedHeight = (verticalCells * (cellLength + borderWidth)) +
                borderWidth;

        int skipX = (getWidth()  - occupiedWidth) / 2;
        int skipY = (getHeight() - occupiedHeight) / 2;

        g.setColor(borderColor);
        //Draw horizontal borders
        for (int y = 0; y <= verticalCells; ++y) {
            g.fillRect(skipX,
                    skipY + y * (borderWidth + cellLength),
                    horizontalCells * (borderWidth + cellLength) + borderWidth,
                    borderWidth);
        }

        g.setColor(borderColor);
        // Draw vertical borders.
        for (int x = 0; x <= horizontalCells; ++x) {
            g.fillRect(skipX + x * (borderWidth + cellLength),
                    skipY,
                    borderWidth,
                    verticalCells * (borderWidth + cellLength) + borderWidth);
        }
        g.setColor(majorborderColor);
        //Draw major borders
        for (int y = 0; y <= 3; ++y) {
            g.fillRect(skipX,
                    skipY + (y * 3) * (borderWidth + cellLength),
                    horizontalCells * (borderWidth + cellLength) + borderWidth,
                    borderWidth);
            g.fillRect(skipX + (y * 3) * (borderWidth + cellLength),
                    skipY,
                    borderWidth,
                    verticalCells * (borderWidth + cellLength) + borderWidth);
        }


        if (highlightCellX >= 0
                && highlightCellX < horizontalCells
                && highlightCellY >= 0
                && highlightCellY < verticalCells) {
            boolean cellOccupied = currentGrid.getCell(highlightCellX,
                    highlightCellY) != 0;
            Point p = currentGrid.getLastplayed();
            if (cellOccupied) {
                g.setColor(this.highlightOccupiedBorderColor);
            } else {
                if ((p.x == Math.floor((double) highlightCellX / 3.0) &&
                        p.y == Math.floor((double) highlightCellY / 3.0)) ||
                        (p.x == -1 && p.y == -1) ){
                    g.setColor(this.highlightFreeBorderColor);
                } else {
                    g.setColor(this.highlightOccupiedBorderColor);
                }
            }

            // Draw the border.
            g.fillRect(skipX + highlightCellX * (borderWidth + cellLength),
                    skipY + highlightCellY * (borderWidth + cellLength),
                    2 * borderWidth + cellLength,
                    2 * borderWidth + cellLength);
            if (cellOccupied) {
                g.setColor(this.highlightOccupiedCellColor);
            } else {
                if ((p.x == Math.floor((double) highlightCellX / 3.0) &&
                        p.y == Math.floor((double) highlightCellY / 3.0)) ||
                (p.x == -1 && p.y == -1) ) {
                    g.setColor(this.highlightFreeCellColor);
                } else {
                    g.setColor(this.highlightOccupiedCellColor);
                }
            }

            //Draw the cell
            g.fillRect(skipX + highlightCellX * (borderWidth + cellLength) + borderWidth,
                    skipY + highlightCellY * (borderWidth + cellLength) + borderWidth,
                    cellLength ,
                    cellLength);
        }

        g.setColor(foregroundColor);
        g.setFont(getFont());

        if (currentGrid == null) {
            return;
        }

        int verticalSkip = 16;

        Font font = prepareFont(cellLength, verticalSkip, g);

        g.setFont(font);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g.getFontMetrics(font);

        int textHeight = fm.getAscent();
        int textWidth = fm.stringWidth("X");

        int dx = (cellLength - textWidth)  / 2;
        int dy = (cellLength - textHeight) / 2;
        for (int y = 0; y < currentGrid.getHeight(); ++y) {
            for (int x = 0; x < currentGrid.getWidth(); ++x) {
                int player = currentGrid.getCell(x,y);
                String mark;
                if (player == 0) {
                    continue;
                } else {
                    //System.out.println("I AM DRAWING A STRING");
                    if (player == 1) {mark = "X";}
                    else { mark = "O"; }
                }
                g.drawString(mark,
                        skipX + dx + borderWidth * (1 + x) + x * cellLength,
                        skipY - dy - 8 + borderWidth * (1 + y) + (1 + y) * cellLength);
            }
        }

        int widthofsubgame = (borderWidth * 3) + (cellLength * 3);
        g2.setStroke(new BasicStroke(borderWidth * 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        //Draw winner x's
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (currentGrid.checkWinners(x,y) != 0) {
                    int tlx = skipX + ( widthofsubgame * x );
                    int tly = skipY + ( widthofsubgame * y );
                    //System.out.println("Putting square at " + tlx + "," + tly + "," + widthofsubgame + "px");
                    if (currentGrid.checkWinners(x, y) == 1) {
                        //Drawing an X
                        g2.setColor(playerOneColor);
                        g2.drawLine(tlx + borderWidth,tly + borderWidth,tlx+widthofsubgame - borderWidth,tly+widthofsubgame - borderWidth);
                        g2.drawLine(tlx+widthofsubgame - borderWidth,tly + borderWidth,tlx + borderWidth,tly+widthofsubgame - borderWidth);
                    } else if (currentGrid.checkWinners(x, y) == 2) {
                        g2.setColor(playerTwoColor);
                        Shape circle = new Ellipse2D.Float(tlx + borderWidth,tly + borderWidth,widthofsubgame - borderWidth,widthofsubgame - borderWidth);
                        g2.draw(circle);
                    }

                    g2.fillRect(tlx,tly, widthofsubgame + borderWidth, widthofsubgame + borderWidth);
                }
            }
        }

        //Draw out of play (grey) squares
        g2.setColor(outOfPlayColor);
        Point p = currentGrid.getLastplayed();
        if (p.x == -1 && p.y == -1) {
            //Then we have no out of play squares
        } else {
            if (end) {
              //All squares are out of play if game has ended
              for (int y = 0; y < 3; y++) {
                  for (int x = 0; x < 3; x++) {
                      int tlx = skipX + (widthofsubgame * x);
                      int tly = skipY + (widthofsubgame * y);
                      g2.fillRect(tlx, tly, widthofsubgame + borderWidth, widthofsubgame + borderWidth);
                  }
              }
            } else {
              //Otherwise just fill squares that are out of play
              for (int y = 0; y < 3; y++) {
                  for (int x = 0; x < 3; x++) {
                      if (p.x == x && p.y == y) {

                      } else {
                          int tlx = skipX + (widthofsubgame * x);
                          int tly = skipY + (widthofsubgame * y);
                          g2.fillRect(tlx, tly, widthofsubgame + borderWidth, widthofsubgame + borderWidth);
                      }
                  }
              }
            }
        }
    }

    private Font prepareFont(int cellLength, int verticalSkip, Graphics g) {
        Font currentFont = getFont();

        for (int fontSize = 1; ; ++fontSize) {
            Font f = new Font(currentFont.getFontName(), Font.BOLD, fontSize);
            FontMetrics fm = g.getFontMetrics(f);

            int height = fm.getAscent();

            if (height >= cellLength - verticalSkip) {
                return new Font(currentFont.getFontName(),
                        Font.BOLD,
                        fontSize - 1);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        update(g);
    }

    private Point toCellCoordinates(int x, int y) {
        Point ret = new Point();
        int availableWidth = getWidth() - 2 * padding;
        int availableHeight = getHeight() - 2 * padding;

        int horizontalCells = currentGrid.getWidth();
        int verticalCells = currentGrid.getHeight();

        int cellWidth   = (availableWidth - (horizontalCells + 1) * borderWidth) / horizontalCells;
        int cellHeight  = (availableHeight - (verticalCells + 1) * borderWidth) / verticalCells;

        int cellLength = Math.min(cellWidth, cellHeight);
        int occupiedWidth = (horizontalCells * ( cellLength + borderWidth)) + borderWidth;
        int occupiedHeight = (verticalCells * (cellLength + borderWidth)) + borderWidth;

        x -= (getWidth() - occupiedWidth) / 2;
        y -= (getHeight() - occupiedHeight) / 2;
        if ((x % (cellLength + borderWidth) < borderWidth)
                || (y % (cellLength + borderWidth) < borderWidth)) {
            ret.x = -1;
            return ret;
        }
        ret.x = x / (cellLength + borderWidth);
        ret.y = y / (cellLength + borderWidth);

        return ret;
    }

    private void tryClick(int x, int y, boolean translatecoords) {
        if (!end) {
          Point p;
          if (translatecoords == true) {
              p = toCellCoordinates(x, y);
          } else {
              p = new Point(x,y);
          }
          //System.out.println("point: " + p.x + "," + p.y);
          if (p.x >= 0 && p.x < currentGrid.getWidth()) {
              Point prev = currentGrid.getLastplayed();
              if ((prev.x == Math.floor((double)p.x / 3.0) &&
                      prev.y == Math.floor((double)p.y / 3.0)) ||
                      (prev.x == -1 && prev.y == -1) ) {
                  if (currentGrid.setCell(p.x, p.y, currentplayer) == true) {
                      if (currentplayer == 1) {
                          listener.changelabel("O to play");
                          currentplayer = 2;
                      } else {
                          listener.changelabel("X to play");
                          currentplayer = 1;
                      }
                  }
              }
              int winner = currentGrid.checkVictory();
              if (winner == 1) {
                end = true;
                listener.changelabel("X Wins!");
                // currentGrid.printwinners();
            } else if (winner == 2) {
                end = true;
                listener.changelabel("O Wins!");
            }
              //System.out.println(currentGrid.getCell(p.x,p.y));
              repaint();
          }
        }
    }

    private void tryHighlight(int x, int y) {
        if (!end) {
          Point p = toCellCoordinates(x,y);
          if (p.x < 0) {
              if (previousHighlightCellX != p.x) {
                  highlightCellX = p.x;
                  previousHighlightCellX = p.x;
                  repaint();
              }
              return;
          }

          if (p.x >= 0 && p.x < currentGrid.getWidth()) {
              this.lastValidCellX = p.x;
              this.lastValidCellY = p.y;
          }

          this.highlightCellX = p.x;
          this.highlightCellY = p.y;

          if (highlightCellX != previousHighlightCellX
                  || highlightCellY != previousHighlightCellY) {
              previousHighlightCellX = highlightCellX;
              previousHighlightCellY = highlightCellY;
              repaint();
        }
      }
    }

    private class CanvasKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            dokey(e);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            dokey(e);
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }

        private void dokey(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (lastValidCellY > 0) {
                        previousHighlightCellY = lastValidCellY;
                        highlightCellY = --lastValidCellY;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                 case KeyEvent.VK_D:
                    if (lastValidCellX < currentGrid.getWidth() - 1) {
                        previousHighlightCellX = lastValidCellX;
                        highlightCellX = ++lastValidCellX;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (lastValidCellY < currentGrid.getHeight() - 1) {
                        previousHighlightCellY = lastValidCellY;
                        highlightCellY = ++lastValidCellY;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (lastValidCellX > 0) {
                        previousHighlightCellX = lastValidCellX;
                        highlightCellX = --lastValidCellX;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    tryClick(highlightCellX,highlightCellY,false);
                    break;
                case KeyEvent.VK_C:
                    Point p = currentGrid.findcompspot(0);
                    tryClick(p.x,p.y,false);
                    break;
            }
        }
    }

    private class CanvasMouseListener implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            tryClick(e.getX(),e.getY(),true);
        }
        @Override
        public void mousePressed(MouseEvent e) {

        }
        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            tryHighlight(e.getX(), e.getY());
        }
    }
}
