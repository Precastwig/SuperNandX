import java.awt.*;
import java.util.ArrayList;
import java.util.jar.Pack200;
import java.util.*;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;


public class AI extends NotifyingThread {
	private int ability;
	private GameGrid game;
	private int player;
	private Point ret;

	public AI(int abilityz, int playerz, GameGrid gamez) {
		 ability = abilityz;
		 game = gamez;
		 player = playerz;
		 ret = new Point(-1,-1);
	}

	public Point getReturn() {
		return ret;
	}

 	public void doRun() {
		// try {
			findcompspot();
    	// } catch (InterruptedException e) {
        // 	System.out.println("Thread interrupted.");
    	// }
	}

	public Point findcompspot() {
        int gx=0,gy=0,x=0,y=0;
        if (ability == 0) {
            //Find empty totally random spot
            if (game.lastplayed.x == -1 && game.lastplayed.y == -1) {
              //If we have an all choice then pick a supersquare at random
            	do {
            		gx = ThreadLocalRandom.current().nextInt(0,game.width);
            		gy = ThreadLocalRandom.current().nextInt(0,game.height);
				} while (game.grid[gx][gy].isfull());
            } else {
            	//Otherwise we have no choice of supersquare
            	gx = game.lastplayed.x;
            	gy = game.lastplayed.y;
            	// System.out.println(gx + " " + gy);
            }

            //If our choice is full for some reason (end of game)
            if (game.grid[gx][gy].isfull()) {
                //Ignore
            } else {
              	// Otherwise choose a subsquare
	            do {
                x = ThreadLocalRandom.current().nextInt(0,game.width);
                y = ThreadLocalRandom.current().nextInt(0,game.height);
                // System.out.println(gx + " " + gy + " [" + x + " " + y + "] last played = " + lastplayed.x + " " + lastplayed.y);
				} while (game.grid[gx][gy].getCell(x,y) != 0);
            }
		} else {
            ret = minmax(-2000,2000,ability, player, player, game).returnmove;
            if (ret == null) {
                ret = new Point(-1,-1);
            }
        }

        //Return the translated coords
        return reverseTranslateCoords(gx,gy,x,y,game);
    }

	private int[] translateCoords(int x, int y, GameGrid game) {
        int[] ret = new int[4];
        int gx = (int) Math.floor((double)x / (double)game.width);
        int gy = (int) Math.floor((double)y / (double)game.height);
        int xs = gx * game.width;
        int ys = gy * game.height;
        xs = x - xs;
        ys = y - ys;
        ret[0] = gx;
        ret[1] = gy;
        ret[2] = xs;
        ret[3] = ys;
        return ret;
    }

	private Point reverseTranslateCoords(int gx, int gy, int x, int y, GameGrid game) {
        Point ret = new Point(0,0);
        ret.x = (gx * game.width) + x;
        ret.y = (gy * game.height) + y;
        return ret;
    }

	private ArrayList<Point> generatemoves(GameGrid game) {
        ArrayList<Point> ret = new ArrayList<Point>();
        if (game.lastplayed.x == -1 && game.lastplayed.y == -1) {
            for (int y = 0; y < game.width * game.height; y++) {
                for (int x = 0; x < game.width * game.height; x++) {
                    if (game.getCell(x,y) == 0)
                        ret.add(new Point(x,y));
                }
            }
        } else {
            for (int y = 0; y < game.height; y++) {
                for (int x = 0; x < game.width; x++) {
                    if (game.grid[game.lastplayed.x][game.lastplayed.y].getCell(x,y) == 0)
                        ret.add(reverseTranslateCoords(game.lastplayed.x,game.lastplayed.y,x,y,game));
                }
            }
        }
        return ret;
    }

	public static float evaluate(int turn, GameGrid game) {
		int opp = 0;
        if (turn == 1)
            opp = 2;
        else
            opp = 1;
		float winsscore = (float)game.winners.countplayer(turn);
		float losescore = (float)game.winners.countplayer(opp);

		float linescore = game.winners.countalmostline(turn);
		float loselinescore = (float)game.winners.countalmostline(opp);

        float subgamescore = 0;
        float losesubgame = 0;
		for (int y = 0; y < game.height; y++) {
            for (int x = 0; x < game.width; x++) {
                if (game.winners.getCell(x,y) == 0) {
                    subgamescore += game.grid[x][y].countalmostline(turn) * 0.5;
                    losesubgame += game.grid[x][y].countalmostline(opp) * 0.5;
                }
            }
        }

        int victory = game.winners.checkwin();
        float vpoints = 0;
        if (victory == turn)
            vpoints = 100;
        else if (victory != 0)
            vpoints = -100;

        return winsscore - losescore + linescore - loselinescore + subgamescore - losesubgame + vpoints;
	}

	private MoveValue minmax(float alpha, float beta, int maxdepth, int player, int currentplayer, GameGrid game) {
        ArrayList<Point> moves = generatemoves(game);
        if (moves.isEmpty()) {
            return new MoveValue();
        }
        // System.out.println(moves);
        // this.printgrid();
        Iterator<Point> movesIt = moves.iterator();
        boolean isMax = currentplayer == player;
        float value = 0;
        if (maxdepth == 0 || game.winners.checkwin() != 0) {
            value = this.evaluate(player, game);
            return new MoveValue(value);
            //Game over stop searching
        } else {
            //Carry on..
            MoveValue returnmove;
            MoveValue bestmove = null;
            if (isMax) {
                while(movesIt.hasNext()) {
                    Point currentmove = movesIt.next();
                    GameGrid depthgrid = new GameGrid(game);
                    depthgrid.setCell(currentmove.x,currentmove.y, currentplayer);
                    if (currentplayer == 1)
                        returnmove = minmax(alpha,beta, maxdepth - 1, player, 2, depthgrid);
                    else
                        returnmove = minmax(alpha,beta, maxdepth - 1, player, 1, depthgrid);

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
                    GameGrid depthgrid = new GameGrid(game);
                    depthgrid.setCell(currentmove.x,currentmove.y, currentplayer);
                    if (currentplayer == 1)
                        returnmove = minmax(alpha,beta, maxdepth - 1, player, 2,depthgrid);
                    else
                        returnmove = minmax(alpha,beta, maxdepth - 1, player, 1,depthgrid);

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
}
