package ubc.GameState;

import java.util.Arrays;

public class Board {

    public final int EMPTY = 0, WHITE = 1, BLACK = 2, ARROW = 3, BOARD_SIZE = 10;

    // Assuming starting position is always the same, as below (white always starts at the bottom)
    private int[][] gameBoard = {
        {0,0,0,2,0,0,2,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {2,0,0,0,0,0,0,0,0,2},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,1},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,0,0,1,0,0,0},
    };

    private boolean isBlack;

    // Storing queens in a 2D array, where each queen is a 1D array in the following format: {row,col}
    private int[][] friendlyQueens;
    private int[][] enemyQueens;

    public boolean debugMode = true;

    // Constructor used when we receive game-state-board message to create a new board from the game state 
    public Board(boolean playerIsBlack) {
        
        friendlyQueens = new int[4][2];
        enemyQueens = new int[4][2];

        int friendlyQueensIndex = 0, enemyQueensIndex = 0;

        // Iterate through the board:
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){

                // If the square has a black queen
                if(this.gameBoard[i][j] == BLACK){
                    if(playerIsBlack)
                        friendlyQueens[friendlyQueensIndex++] = new int[]{i,j};
                    else enemyQueens[enemyQueensIndex++] = new int[]{i,j}; 
                }
                
                // If the square has a white queen
                else if(this.gameBoard[i][j] == WHITE){
                    if(playerIsBlack)
                        enemyQueens[enemyQueensIndex++] = new int[]{i,j};
                    else friendlyQueens[friendlyQueensIndex++] = new int[]{i,j};
                }
            }
        }

        if(debugMode)
            System.out.println("Team queens: " + Arrays.deepToString(friendlyQueens) + "\nEnemy queens: " + Arrays.deepToString(enemyQueens));
    }

    // Constructor used to create a new board from the existing board + a move
    public Board(Board oldBoard, Move move) {
        
        if(debugMode)
            System.out.println("Before move: \n" + toString());

        this.gameBoard = oldBoard.gameBoard;
        this.friendlyQueens = oldBoard.friendlyQueens;
        this.enemyQueens = oldBoard.enemyQueens;

        int newX = move.getNewPos().get(0);
        int newY = move.getNewPos().get(1);
        int oldX = move.getOldPos().get(0);
        int oldY = move.getOldPos().get(1);

        // Get value of the queen that is to be moved
        int current = gameBoard[oldX][oldY];

        // Update the board
        gameBoard[newX][newY] = current;
        gameBoard[oldX][oldY] = EMPTY;
        gameBoard[move.getArrowPos().get(0)][move.getArrowPos().get(1)] = ARROW;

        // Update the queen that moved
        if (isFriendly(current)) {

            // Check existing friendly queens to find which one has the same starting position
            for (int i = 0; i < friendlyQueens.length; i++) {
                if (friendlyQueens[i][0] == oldX && friendlyQueens[i][1] == oldY) {
                    friendlyQueens[i] = new int[]{newX, newY};
                    break;
                }
            }
        } else {

            // Check existing enemy queens to find which one has the same starting position
            for (int i = 0; i < enemyQueens.length; i++) {
                if (enemyQueens[i][0] == oldX && enemyQueens[i][1] == oldY) {
                    enemyQueens[i] = new int[]{newX, newY};
                    break;
                }
            }
        }

        if(debugMode)
            System.out.println("--------------------------------------\nAfter move: \n" + toString());
    }

    // Function used to check if a piece is friendly
    public boolean isFriendly(int color){
        return isBlack ? color == BLACK : color == WHITE;
    }

    // Function to check if a square is empty
    public boolean isEmpty(int row, int col){
        return gameBoard[row][col] == EMPTY;
    }

    public boolean moveQueenOnGameBoard(int[] queen, int[] square){

        int oldX = queen[0], oldY = queen[1], newX = square[0], newY = square[1];

        if (!isEmpty(newX, newY) || isEmpty(oldX, oldY) || getPos(oldX, oldY) == ARROW) return false;
        
        int color = getPos(oldX, oldY);
        this.gameBoard[oldX][oldY] = EMPTY;
        this.gameBoard[newX][newY] = color;  

        return true;
        
    }

    public int[][] getFriendlyQueens() {
        return this.friendlyQueens;
    }

    public int[][] getEnemyQueens() {
        return this.enemyQueens;
    }

    public int getPos(int row, int col) {
        return gameBoard[row][col];
    }

    public boolean isBlack() {
        return isBlack;
    }

    @Override
    public String toString() {
        // we use deepToString since board is a 2D array
        return Arrays.deepToString(gameBoard)
                        .replace("[[", "[").replace("]]", "]") //remove the extra brackets at the start and end
                        .replace("], ", "]\n") //since it is a 2D array, replace the comma and space with a newline
                        .replace("2", "B").replace("1", "W").replace("3", "X");  //replace the numbers with letters
    }
}