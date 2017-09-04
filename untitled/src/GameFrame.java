import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

    private int highlightCellX;
    private int highlightCellY;

    private int previousHighlightCellX;
    private int previousHighlightCellY;

    private int lastValidCellX = -1;
    private int lastValidCellY = -1;

    private GameGrid currentGrid;
    private int currentplayer = 1;

    public GameFrame() {
        this.padding = DEFAULT_PADDING;
        this.borderWidth = DEFAULT_BORDER_WIDTH;
        this.backgroundColor = Color.WHITE;
        this.borderColor = Color.lightGray;
        this.majorborderColor = Color.BLACK;
        this.foregroundColor = Color.BLACK;
        this.highlightFreeBorderColor = new Color(100,200,100);
        this.highlightFreeCellColor = Color.GREEN;
        this.highlightOccupiedBorderColor = Color.PINK;
        this.highlightOccupiedCellColor = Color.RED;
        setFont(DEFAULT_FONT);

        currentGrid = new GameGrid();
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
            if (cellOccupied) {
                g.setColor(this.highlightOccupiedBorderColor);
            } else {
                g.setColor(this.highlightFreeBorderColor);
            }

            // Draw the border.
            g.fillRect(skipX + highlightCellX * (borderWidth + cellLength),
                    skipY + highlightCellY * (borderWidth + cellLength),
                    2 * borderWidth + cellLength,
                    2 * borderWidth + cellLength);
            if (cellOccupied) {
                g.setColor(this.highlightOccupiedCellColor);
            } else {
                g.setColor(this.highlightFreeCellColor);
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

        //Font font = prepareFont(cellLength, verticalSkip, g);

        g.setFont(getFont());

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g.getFontMetrics(getFont());

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

        //Draw winner x's
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                String mark;
                if(currentGrid.checkWinners(x,y) == 1) {mark = "X";} else
                if(currentGrid.checkWinners(x,y) == 2) {mark = "O";}
                else {
                    continue;
                }
                g.drawString(mark,
                        skipX + dx + borderWidth * (1 + (x * 3)) + x * cellLength,
                        skipY - dy - 8 + borderWidth * (1 + (y * 3) + (1 + y * 3) * cellLength));
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
        Point p;
        if (translatecoords == true) {
            p = toCellCoordinates(x, y);
        } else {
            p = new Point(x,y);
        }
        //System.out.println("point: " + p.x + "," + p.y);
        if (p.x >= 0 && p.x < currentGrid.getWidth()) {
            if (currentGrid.setCell(p.x,p.y,currentplayer) == false) {
                //Put stuff here?
            }
            //System.out.println(currentGrid.getCell(p.x,p.y));
            if (currentplayer == 1) {
                currentplayer = 2;
            } else {
                currentplayer = 1;
            }
            repaint();
        }
    }

    private void tryHighlight(int x, int y) {
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
