/**
 * Created by Precastwig on 02/09/2017.
 */
public class TwoDee {
    private int[][] array;
    private int width;
    private int height;
    private int winner = 0;

    public TwoDee(int width, int height) {
        array = new int[width][height];
        this.width = width;
        this.height = height;
    }
    // 0 is an unset cell
    // 1 is X
    // 2 is O

    public boolean setCell(int x, int y, int player) {
        //System.out.println("Setting " + x + "," + y + " to " + player);
        if (x < 0 || x >= width || y < 0 || y >= height) {
            System.out.println("Out of bounds error");
            return false;
            // Out of bounds error
        } else {
            if (array[x][y] == 0) {
                array[x][y] = player;
                checkwin();
                return true;
                //success
            } else {
                System.out.println("Cell already set error");
                return false;
                // Already set cell
            }
        }
    }

    public int getCell(int x, int y) {
        // Check if they're getting a nonexistent cell
        if (x > width || x < 0 || y > height || y < 0) {
            throw new RuntimeException();
        }
        return array[x][y];
    }

    public void reset() {
        // Does what it says on the tin
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                array[i][j] = 0;
            }
        }
    }

    public void printGame() {
        //Prints it poorly for debugging
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(" " + array[x][y] + " ");
                if (x == height -1) {
                    System.out.println();
                }
            }
        }
    }

    public int checkwin() {
        if (winner == 0) {
            for (int i = 0; i < width; i++) {
                if (array[i][0] == array[i][1] && array[i][1] == array[i][2] && array[i][0] != 0) {
                    winner = array[i][0];
                    return winner;
                }
                if (array[0][i] == array[1][i] && array[1][i] == array[2][i] && array[0][i] != 0) {
                    winner = array[0][i];
                    return winner;
                }
            }
            if (array[0][0] == array[1][1] && array[1][1] == array[2][2] && array[0][0] != 0) {
                winner = array[0][0];
                return winner;
            }
            if (array[2][0] == array[1][1] && array[1][1] == array[0][2] && array[2][0] != 0) {
                winner = array[2][0];
                return winner;
            }
            return winner;
        } else {
            return winner;
        }
    }

    public boolean isfull() {
      for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
              if (array[x][y] == 0) {
                  return false;
              }
          }
      }
      return true;
    }
}
