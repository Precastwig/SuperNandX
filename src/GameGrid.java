import java.awt.*;
import java.util.ArrayList;
import java.util.jar.Pack200;

/**
 * Created by Precastwig on 02/09/2017.
 */
public class GameGrid {

    private static int DEFAULT_SIZE = 3;
    private int width = DEFAULT_SIZE;
    private int height = DEFAULT_SIZE;
    private TwoDee[][] grid = new TwoDee[width][height];
    private int[][] winners = new int[width][height];
    private Point lastplayed = new Point(-1,-1);

    public GameGrid() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new TwoDee(width,height);
            }
        }
    }

    public void resetGrid() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j].reset();
            }
        }
    }

    private void updateWinners() {
        //System.out.println("Updating winners");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (winners[i][j] == 0) { //Makes sure the first winner of a square is the winner
                    //System.out.println("Checking subgame " + i + "," + j);
                    //grid[i][j].printGame();
                    winners[i][j] = grid[i][j].checkwin();
                    //System.out.println(winners[i][j]);
                }
            }
        }
    }

    public int checkWinners(int x, int y) {
        return winners[x][y];
    }

    public int[][] getWinners() {
        return winners;
    }

    public int checkWin() {
        int winner = 0;
        updateWinners();
        for (int i = 0; i < width; i++) {
            if (winners[i][0] == winners[i][1] && winners[i][1] == winners[i][2]) {
                winner = winners[i][0];
            }
            if (winners[0][i] == winners[1][i] && winners[1][i] == winners[2][i]) {
                winner = winners[0][i];
            }
        }
        if (winners[0][0] == winners[1][1] && winners[1][1] == winners[2][2]) {
            winner = winners[0][0];
        } else if (winners[2][0] == winners[1][1] && winners[1][1] == winners[0][2]) {
            winner = winners[2][0];
        }
        return winner;
    }

    private int[] translateCoords(int x, int y) {
        int[] ret = new int[4];
        int gx = (int) Math.floor((double)x / (double)width);
        int gy = (int) Math.floor((double)y / (double)height);
        int xs = gx * width;
        int ys = gy * height;
        xs = x - xs;
        ys = y - ys;
        ret[0] = gx;
        ret[1] = gy;
        ret[2] = xs;
        ret[3] = ys;
        return ret;
    }

    private void printwinners() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(" " + winners[j][i] + " ");
                if (j == height - 1) {
                    System.out.println();
                }
            }
        }
    }

    public Point getLastplayed(){
        return lastplayed;
    }

    public boolean setCell(int x, int y, int player) {
        int[] coords = translateCoords(x,y);
        return setCell(coords[0],coords[1],coords[2],coords[3],player);
    }

    public boolean setCell(int gx, int gy, int x, int y, int player) {
        if (grid[gx][gy].getCell(x,y) == 0) {
            if (grid[x][y].isfull()) {
              lastplayed.x = -1;
              lastplayed.y = -1;
            } else {
              lastplayed.x = x;
              lastplayed.y = y;
            }
            grid[gx][gy].setCell(x, y, player);
            updateWinners();
            //printwinners();
            return true;
        } else {
            return false;
        }
    }

    public int getCell(int x, int y) {
        int[] coords = translateCoords(x,y);
        return getCell(coords[0],coords[1],coords[2],coords[3]);
    }

    public int getCell(int gx, int gy, int x, int y) {
        return grid[gx][gy].getCell(x,y);
    }

    public int getWidth() {
        return width * width;
    }
    public int getHeight() {
        return height * height;
    }
}
