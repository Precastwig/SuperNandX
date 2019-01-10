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
    //TODO MAKE WINNING IN LAST OF A LINE SCORE HIGHER
    public Point getLineWinner() {
        return winners.findwin();
    }

    public float evaluate(int turn) {
        int opp = 0;
        if (turn == 1)
            opp = 2;
        else
            opp = 1;
		float winsscore = (float)winners.countplayer(turn);
		float losescore = (float)winners.countplayer(opp);

		float linescore = winners.countalmostline(turn);
		float loselinescore = (float)winners.countalmostline(opp);

        float subgamescore = 0;
        float losesubgame = 0;
		for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (winners.getCell(x,y) == 0) {
                    subgamescore += grid[x][y].countalmostline(turn) * 0.5;
                    losesubgame += grid[x][y].countalmostline(opp) * 0.5;
                }
            }
        }

        int victory = this.checkVictory();
        float vpoints = 0;
        if (victory == turn)
            vpoints = 100;
        else if (victory != 0)
            vpoints = -100;

        return winsscore - losescore + linescore - loselinescore + subgamescore - losesubgame + vpoints;

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

    private ArrayList<Point> generatemoves() {
        ArrayList<Point> ret = new ArrayList<Point>();
        if (lastplayed.x == -1 && lastplayed.y == -1) {
            for (int y = 0; y < width * height; y++) {
                for (int x = 0; x < width * height; x++) {
                    if (this.getCell(x,y) == 0)
                        ret.add(new Point(x,y));
                }
            }
        } else {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (this.getCell(lastplayed.x,lastplayed.y,x,y) == 0)
                        ret.add(reverseTranslateCoords(lastplayed.x,lastplayed.y,x,y));
                }
            }
        }
        return ret;
    }

    private MoveValue minmax(float alpha, float beta, int maxdepth, int player, int currentplayer) {
        ArrayList<Point> moves = generatemoves();
        if (moves.isEmpty()) {
            return new MoveValue();
        }
        // System.out.println(moves);
        // this.printgrid();
        Iterator<Point> movesIt = moves.iterator();
        boolean isMax = currentplayer == player;
        float value = 0;
        if (maxdepth == 0 || this.checkVictory() != 0) {
            value = this.evaluate(player);
            return new MoveValue(value);
            //Game over stop searching
        } else {
            //Carry on..
            MoveValue returnmove;
            MoveValue bestmove = null;
            if (isMax) {
                while(movesIt.hasNext()) {
                    Point currentmove = movesIt.next();
                    GameGrid depthgrid = new GameGrid(this);
                    depthgrid.setCell(currentmove.x,currentmove.y, currentplayer);
                    if (currentplayer == 1)
                        returnmove = depthgrid.minmax(alpha,beta, maxdepth - 1, player, 2);
                    else
                        returnmove = depthgrid.minmax(alpha,beta, maxdepth - 1, player, 1);

                    depthgrid.setCell(currentmove.x,currentmove.y, 0);
                    // board.undoLastMove(); ??
                    if ( (bestmove == null) || (bestmove.returnvalue < returnmove.returnvalue)) {
                        bestmove = returnmove;
                        bestmove.returnmove = currentmove;
                    }
                    if (returnmove.returnvalue > alpha) {
                        alpha = returnmove.returnvalue;
                        bestmove = returnmove;
                    }
                    if (beta <= alpha) {
                        //the pruning bit
                        bestmove.returnvalue = beta;
                        bestmove.returnmove = null;
                        return bestmove;
                    }
                }
                // this.printgrid();
                return bestmove;
            } else {
                while(movesIt.hasNext()) {
                    Point currentmove = movesIt.next();
                    GameGrid depthgrid = new GameGrid(this);
                    depthgrid.setCell(currentmove.x,currentmove.y, currentplayer);
                    if (currentplayer == 1)
                        returnmove = depthgrid.minmax(alpha,beta, maxdepth - 1, player, 2);
                    else
                        returnmove = depthgrid.minmax(alpha,beta, maxdepth - 1, player, 1);

                    depthgrid.setCell(currentmove.x,currentmove.y, 0);
                    if ((bestmove == null) || (bestmove.returnvalue > returnmove.returnvalue)) {
                        bestmove = returnmove;
                        bestmove.returnmove = currentmove;
                    }
                    if (returnmove.returnvalue < beta) {
                        beta = returnmove.returnvalue;
                        bestmove = returnmove;
                    }
                    if (beta <= alpha) {
                        bestmove.returnvalue = alpha;
                        bestmove.returnmove = null;
                        return bestmove;
                    }
                }
                // this.printgrid();
                return bestmove;
            }
        }
    }

    // BEGIN FINDING BEST NEXT LOCATION
    // NOT SURE HOW YET
    // ability 0 = random
    //         1 = minmax
    public Point findcompspot(int ability, int player, int depth) {
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
                  // System.out.println(gx + " " + gy);
                }

                //If our choice is full for some reason (end of game)
                if (grid[gx][gy].isfull()) {
                    //Ignore
                } else {
                  // Otherwise choose a subsquare
                  do {
                    x = ThreadLocalRandom.current().nextInt(0,width);
                    y = ThreadLocalRandom.current().nextInt(0,height);
                    // System.out.println(gx + " " + gy + " [" + x + " " + y + "] last played = " + lastplayed.x + " " + lastplayed.y);
                  } while (grid[gx][gy].getCell(x,y) != 0);
                }
                break;
            case 1:
                Point ret = minmax(-2000,2000,depth, player, player).returnmove;
                if (ret != null) {
                    return ret;
                } else {
                    return new Point(-1,-1);
                }
                //Check with a depth of 1 the next moves value according to evaluate
                // GameGrid depthgrid = new GameGrid(this);
                // Point best = new Point(-1,-1);
                // float top = 0;
                // if (lastplayed.x == -1 && lastplayed.y == -1) {
                //     //Check all moves why not
                //     for (y = 0; y < height * width; y++) {
                //         for (x = 0; x < width * height; x++) {
                //             if (depthgrid.getCell(x,y) == 0) {
                //                 depthgrid.setCell(x,y,player);
                //                 float current = depthgrid.evaluate(player);
                //                 if (current > top) {
                //                     top = current;
                //                     best.x = x;
                //                     best.y = y;
                //                 } else {
                //                     depthgrid.setCell(x,y,0);
                //                 }
                //             }
                //         }
                //     }
                // } else {
                //     for (y = 0; y < height; y++) {
                //         for (x = 0; x < width; x++) {
                //             if (depthgrid.getCell(lastplayed.x, lastplayed.y, x, y) == 0) {
                //                 depthgrid.setCell(lastplayed.x, lastplayed.y, x, y, player);
                //                 float current = depthgrid.evaluate(player);
                //                 if (current > top) {
                //                     top = current;
                //                     best.x = x;
                //                     best.y = y;
                //                 } else {
                //                     depthgrid.setCell(lastplayed.x,lastplayed.y,x,y,0);
                //                 }
                //             }
                //         }
                //     }
                // }
                // depth--;
                // if (best.x == -1 && best.y == -1) {
                //     return this.findcompspot(0,player, depth);
                // }
                // System.out.println(best.x + " " + best.y);
                // System.out.println(lastplayed.x + " " + lastplayed.y);
                // if (depth == 1)
                //     return reverseTranslateCoords(lastplayed.x,lastplayed.y,best.x,best.y);
                // else
                //     return depthgrid.findcompspot(1,player,depth);
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
