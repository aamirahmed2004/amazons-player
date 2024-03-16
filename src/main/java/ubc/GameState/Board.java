package ubc.GameState;

import java.util.Arrays;

public class Board implements Cloneable {

    private final int EMPTY = 0, WHITE = 1, BLACK = 2, ARROW = 3;

    // Assuming starting position is always the same, as below (white always starts at the bottom)
    private int[][] board = {
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
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){

                // If the square has a black queen
                if(this.board[i][j] == BLACK){
                    if(playerIsBlack)
                        friendlyQueens[friendlyQueensIndex++] = new int[]{i,j};
                    else enemyQueens[enemyQueensIndex++] = new int[]{i,j}; 
                }
                
                // If the square has a white queen
                else if(this.board[i][j] == WHITE){
                    if(playerIsBlack)
                        enemyQueens[enemyQueensIndex++] = new int[]{i,j};
                    else friendlyQueens[friendlyQueensIndex++] = new int[]{i,j};
                }
            }
        }

        if(debugMode)
            System.out.println("Team queens: " + friendlyQueens + "\nEnemy queens: " + enemyQueens);
    }

    // Constructor used to create a new board from an existing board + a move
    public Board(Board Board, Move move) {
        try {

            if(debugMode)
                System.out.println("Before move: \n" + toString());

            Board cloned = (Board) Board.clone();
            board = cloned.board;
            friendlyQueens = cloned.friendlyQueens;
            enemyQueens = cloned.enemyQueens;

            int newX = move.getNewPos().get(0);
            int newY = move.getNewPos().get(1);
            int oldX = move.getOldPos().get(0);
            int oldY = move.getOldPos().get(1);

            // Get value of the queen that is to be moved
            int movingQueen = board[oldX][oldY];

            // Update the board
            board[newX][newY] = movingQueen;
            board[oldX][oldY] = EMPTY;
            board[move.getArrowPos().get(0)][move.getArrowPos().get(1)] = ARROW;

            // Update the queen that moved
            if (isFriendly(movingQueen)) {

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

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // Function used to check if a piece is friendly
    public boolean isFriendly(int pieceValue){
        return isBlack ? pieceValue == BLACK : pieceValue == WHITE;
    }

    public int[][] getFriendlyQueens() {
        return this.friendlyQueens;
    }

    public int[][] getEnemyQueens() {
        return this.enemyQueens;
    }

    public int getPos(int row, int col) {
        return board[row][col];
    }

    public boolean isBlack() {
        return isBlack;
    }

    public Object clone() throws CloneNotSupportedException {
        Board clone = (Board) super.clone();
        clone.board = this.board.clone();
        return clone;
    }

    @Override
    public String toString() {
        // we use deepToString since board is a 2D array
        return Arrays.deepToString(board)
                        .replace("[[", "[").replace("]]", "]") //remove the extra brackets at the start and end
                        .replace("], ", "]\n") //since it is a 2D array, replace the comma and space with a newline
                        .replace("2", "B").replace("1", "W").replace("3", "X");  //replace the numbers with letters
    }
}