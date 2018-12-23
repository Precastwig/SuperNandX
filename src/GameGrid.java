import java.awt.*;
import java.util.ArrayList;
import java.util.jar.Pack200;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Precastwig on 02/09/2017.
 */
public class GameGrid {

    private static int DEFAULT_SIZE = 3;
    private int width = DEFAULT_SIZE;
    private int height = DEFAULT_SIZE;
    private TwoDee[][] grid = new TwoDee[width][height];
    private TwoDee winners = new TwoDee(width,height);
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
                if (winners.getCell(i,j) == 0) { //Makes sure the first winner of a square is the winner
                    //System.out.println("Checking subgame " + i + "," + j);
                    //grid[i][j].printGame();
                    winners.setCell(i,j,grid[i][j].checkwin());
                    //System.out.println(winners[i][j]);
                }
            }
        }
    }

    public int checkWinners(int x, int y) {
        return winners.getCell(x,y);
    }

    public TwoDee getWinners() {
        return winners;
    }

    public int checkVictory() {
        int winner = 0;
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

    // BEGIN FINDING BEST NEXT LOCATION
    // NOT SURE HOW YET
    // ability 0 = random
    //         1 = basic tree search?
    public Point findcompspot(int ability) {
        int gx=0,gy=0,x=0,y=0;
        switch (ability) {
          case 0:
            //Find empty totally random spot
            if (lastplayed.x == -1 && lastplayed.y == -1) {
              //If we have an all choice then pick a supersquare at random
              do {
                gx = ThreadLocalRandom.current().nextInt(0,width);
                gy = ThreadLocalRandom.current().nextInt(0,height);
              } while (grid[gx][gy].isfull());
            } else {
              //Otherwise we have no choice of supersquare
              gx = lastplayed.x;
              gy = lastplayed.y;
              System.out.println(gx + " " + gy);
            }

            //If our choice is full for some reason (end of game) just ignore
            if (grid[gx][gy].isfull()) {

            } else {
              // Otherwise choose a subsquare
              do {
                x = ThreadLocalRandom.current().nextInt(0,width);
                y = ThreadLocalRandom.current().nextInt(0,height);
                System.out.println(gx + " " + gy + " [" + x + " " + y + "] last played = " + lastplayed.x + " " + lastplayed.y);
              } while (grid[gx][gy].getCell(x,y) != 0);
              break;
            }
          case 1:
            //Do more advanced things
          break;
        }

        //Return the translated coords
        return reverseTranslateCoords(gx,gy,x,y);
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
