import java.awt.*;
import java.util.ArrayList;
import java.util.jar.Pack200;
import java.util.*;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Precastwig on 02/09/2017.
 */
public class GameGrid {

    private static int DEFAULT_SIZE = 3;
    public int width = DEFAULT_SIZE;
    public int height = DEFAULT_SIZE;
    public TwoDee[][] grid = new TwoDee[width][height];
    public TwoDee winners = new TwoDee(width,height);
    public Point lastplayed = new Point(-1,-1);

    public GameGrid() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new TwoDee(width,height);
            }
        }
    }

    public GameGrid(GameGrid copy) {
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[x][y] = new TwoDee(copy.getsuperCell(x,y));
            }
        }
        winners = new TwoDee(copy.getWinners());
        lastplayed = new Point(copy.getLastplayed());
    }

    public void resetGrid() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j].reset();
            }
        }
        winners.reset();
        // printwinners();
        lastplayed = new Point(-1,-1);
    }

    public void updateWinners() {
        // System.out.println("Updating winners");
        // printwinners();
        // printgrid();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (winners.getCell(i,j) == 0) { //Makes sure the first winner of a square is the winner
                    //System.out.println("Checking subgame " + i + "," + j);
                    //grid[i][j].printGame();
                    winners.setCell(i,j,grid[i][j].checkwin());
                    //System.out.println(winners[i][j]);
                }
            }
        }
        // printwinners();
    }

    public int checkWinners(int x, int y) {
        return winners.getCell(x,y);
    }

    public TwoDee getWinners() {
        return winners;
    }

    public int checkVictory() {
        updateWinners();
        return winners.checkwin();
    }

    //Finds the winning path
    //Returns a point where
    //p.x = 1,2,3: the winning line is a column starting at 1,2,3
    //p.y = 1,2,3: the winning line is a row starting at 1,2,3
    //p.x = 1, p.y = 1: diagonal with positive gradient
    //p.x = 1, p.y = -1: diagonal with negative gradient
    public Point getLineWinner() {
        return winners.findwin();
    }


    //Translates from 0-8,0-8 to 0-2,0-2,0-2,0-2
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

    //Translates from 0-2,0-2,0-2,0-2 to 0-8,0-8
    private Point reverseTranslateCoords(int gx, int gy, int x, int y) {
        Point ret = new Point(0,0);
        ret.x = (gx * width) + x;
        ret.y = (gy * height) + y;
        return ret;
    }

    public void printwinners() {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(" " + winners.getCell(j,i) + " ");
                if (j == height - 1) {
                    System.out.println();
                }
            }
        }
    }

    public void printgrid() {
        System.out.println("--------------------");
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                int[] r = translateCoords(x,y);
                System.out.print(" " + grid[r[0]][r[1]].getCell(r[2],r[3]) + " ");
            }
            System.out.println("");
        }
        System.out.println("--------------------");
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

    public TwoDee getsuperCell(int x, int y) {
        return grid[x][y];
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
