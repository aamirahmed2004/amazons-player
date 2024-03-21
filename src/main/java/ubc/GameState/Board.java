package ubc.GameState;

import java.util.Arrays;

public class Board implements Cloneable {

    public static final byte EMPTY = 0, WHITE = 1, BLACK = 2, ARROW = 3, BOARD_SIZE = 10;

    // Assuming starting position is always the same, as below (white always starts at the bottom, which translates to the right side of the 2D array)
    private byte[][] gameBoard = {
        {0,0,0,1,0,0,2,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,2},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,2},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,0,0,2,0,0,0},
    };     

    private boolean isBlack;

    // Storing queens in a 2D array, where each queen is a 1D array in the following format: {row,col}
    private int[][] friendlyQueens;
    private int[][] enemyQueens;

    public boolean debugMode;

    // Constructor used when we receive game-state-board message to create a new board from the game state 
    public Board(boolean playerIsBlack) {
        
        this.debugMode = true;
        this.friendlyQueens = new int[4][2];
        this.enemyQueens = new int[4][2];
        this.isBlack = playerIsBlack;

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

        // if(debugMode)
        //     System.out.println("Team queens: " + Arrays.deepToString(friendlyQueens) + "\nEnemy queens: " + Arrays.deepToString(enemyQueens));
    }

    // Used to create a new board from an old board + a move (it is now the other player's turn so board.isBlack must be inverted)
    public static Board getNewBoard(Board oldBoard, Move move){

        Board newBoard = new Board();
        Board copy = (Board) oldBoard.clone();
        newBoard.isBlack = !copy.isBlack();
        newBoard.friendlyQueens = copy.enemyQueens;
        newBoard.enemyQueens = copy.friendlyQueens;
        newBoard.makeMove(move);

        return newBoard;
    }

    // Only used for cloning
    public Board(){ 
        this.gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];
        this.friendlyQueens = new int[4][2];
        this.enemyQueens = new int[4][2];
        this.isBlack = false;
    }

    public void makeMove(Move move){
        makeMove(move, false);
    }

    // Constructor used to create a new board from the existing board + a move
    // makeMove(move, true) is only used when receiving opponent's move from game-action-move message 
    public void makeMove(Move move, boolean opponentsMove) {

        if(debugMode){
            System.out.println(notationToString());
            System.out.println("----------------------------------------");
            System.out.println("Before move: \n" + toString());
        }

        int newX = move.getNewPos().get(0);
        int newY = move.getNewPos().get(1);
        int oldX = move.getOldPos().get(0);
        int oldY = move.getOldPos().get(1);
        int arrowX = move.getArrowPos().get(0);
        int arrowY = move.getArrowPos().get(1);

        // Get value of the queen that is to be moved
        byte current = gameBoard[oldX][oldY];

        // Update the board 
        gameBoard[newX][newY] = current;
        gameBoard[oldX][oldY] = EMPTY;
        gameBoard[arrowX][arrowY] = ARROW;

        // Update the queen that moved
        if (isFriendly(current)) {

            // Check existing friendly queens to find which one has the same starting position
            for (int i = 0; i < friendlyQueens.length; i++) {
                if (friendlyQueens[i][0] == oldX && friendlyQueens[i][1] == oldY) {
                    friendlyQueens[i] = new int[]{newX, newY};
                    break;
                }
            }
        } else if (!isFriendly(current)){
            // Check existing enemy queens to find which one has the same starting position
            for (int i = 0; i < enemyQueens.length; i++) {
                if (enemyQueens[i][0] == oldX && enemyQueens[i][1] == oldY) {
                    enemyQueens[i] = new int[]{newX, newY};
                    break;
                }
            }
        } 

        if(debugMode){
            System.out.println("--------------------------------------\nAfter move: \n" + toString());
            System.out.println(notationToString());
        }
    }

    private void printQueens() {
        System.out.println("Friendly queens: " + Arrays.deepToString(friendlyQueens));
        System.out.println("Enemy queens: " + Arrays.deepToString(enemyQueens));
    }

    public Object clone(){

        Board clone = new Board();
        clone.isBlack = this.isBlack;

        // Iterate through the board:
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                clone.gameBoard[i][j] = this.gameBoard[i][j];
            }
        }

        for(int i = 0; i < this.friendlyQueens.length; i++){
            for(int j = 0; j < this.friendlyQueens[i].length; j++){
                clone.friendlyQueens[i][j] = this.friendlyQueens[i][j];
                clone.enemyQueens[i][j] = this.enemyQueens[i][j];
            }
        }

        return clone;
    }

    // Function used to check if a piece is friendly
    public boolean isFriendly(int color){
        return isBlack ? color == BLACK : color == WHITE;
    }

    // Function to check if a square is empty
    public boolean isEmpty(int row, int col){
        return gameBoard[row][col] == EMPTY;
    }

    // Function used to temporarily move queen on board (only for move generation, internal queen positions not updated)
    public boolean moveQueenOnGameBoard(int[] queen, int[] square){

        int oldX = queen[0], oldY = queen[1], newX = square[0], newY = square[1];

        if (!isEmpty(newX, newY) || isEmpty(oldX, oldY) || getPos(oldX, oldY) == ARROW) return false;
        
        byte color = getPos(oldX, oldY);
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

    public byte getPos(int row, int col) {
        return gameBoard[row][col];
    }

    public boolean isBlack() {
        return isBlack;
    }


    //  Don't need this method anymore
    public String notationToString(){

        StringBuilder boardToString = new StringBuilder();
        StringBuilder teamQueenPositions = new StringBuilder();
        StringBuilder enemyQueenPositions = new StringBuilder();

        boardToString.append("");

        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        teamQueenPositions.append("Team queens: [");
        enemyQueenPositions.append("Enemy queens: [");

        for (int x = BOARD_SIZE - 1; x >= 0; x--) { 
            // boardToString.append("[");
            for (int y = 0; y < BOARD_SIZE; y++) {    
                // boardToString.append(letters[y] + (x + 1) + (y == BOARD_SIZE-1 ? "": ", "));
                if (isFriendly(gameBoard[y][x])) {
                    teamQueenPositions.append(letters[y] + (x + 1) + " ");
                }
                else if(!isEmpty(y, x) && !isFriendly(gameBoard[y][x]) && gameBoard[y][x] != ARROW){
                    enemyQueenPositions.append(letters[y] + (x + 1) + " ");
                }
            }
            // boardToString.append("]\n");
        }

        teamQueenPositions.append("]"); enemyQueenPositions.append("]");
        boardToString.append(teamQueenPositions.toString() + "\n" + enemyQueenPositions.toString());

        return boardToString.toString().replace("10", "X");  
    }

    @Override
    public String toString() {
        
        StringBuilder boardToString = new StringBuilder();


        for (int x = BOARD_SIZE - 1; x >= 0; x--) { 
            boardToString.append("[");

            for (int y = 0; y < BOARD_SIZE; y++) { 
                boardToString.append(gameBoard[y][x] + (y == BOARD_SIZE-1 ? "": ", "));
            }
            boardToString.append("]\n");
        }

        return boardToString.toString().replace("1", "W").replace("2", "B").replace("3", "X");
    }
}