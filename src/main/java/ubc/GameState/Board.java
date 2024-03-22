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

    private boolean blackToMove;

    // Storing queens in a 2D array, where each queen is a 1D array in the following format: {row,col}
    // Future: probably change to bitboards for white, black and arrows
    private int[][] blackQueens;
    private int[][] whiteQueens;

    public boolean debugMode;

    // Constructor used when we receive game-state-board message to create a new board from the game state 
    public Board(boolean blackToMove) {
        
        this.debugMode = true;
        this.blackQueens = new int[4][2];
        this.whiteQueens = new int[4][2];
        this.blackToMove = blackToMove;

        int blackQueensIndex = 0, whiteQueensIndex = 0;

        // Iterate through the board:
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){

                // If the square has a black queen
                if(this.gameBoard[i][j] == BLACK){
                    if(blackToMove)
                        blackQueens[blackQueensIndex++] = new int[]{i,j};
                    else whiteQueens[whiteQueensIndex++] = new int[]{i,j}; 
                }
                
                // If the square has a white queen
                else if(this.gameBoard[i][j] == WHITE){
                    if(blackToMove)
                        whiteQueens[whiteQueensIndex++] = new int[]{i,j};
                    else blackQueens[blackQueensIndex++] = new int[]{i,j};
                }
            }
        }
        // if(debugMode)
        //     System.out.println("Team queens: " + Arrays.deepToString(blackQueens) + "\nEnemy queens: " + Arrays.deepToString(whiteQueens));
    }

    // Used to create a new board from an old board + a move (it is now the other player's turn so board.blackToMove must be inverted)
    public Board getNewBoard(Move move){

        Board newBoard = new Board();
        Board copy = (Board) this.clone();
        newBoard.blackQueens = copy.blackQueens;
        newBoard.whiteQueens = copy.whiteQueens;
        newBoard.makeMove(move);

        return newBoard;
    }

    // Only used for cloning
    public Board(){ 
        this.gameBoard = new byte[BOARD_SIZE][BOARD_SIZE];
        this.blackQueens = new int[4][2];
        this.whiteQueens = new int[4][2];
        this.blackToMove = false;
    }

    public void makeMove(Move move){
        makeMove(move,false);
    }
    public void makeMove(Move move, boolean inSearch) {

        if(debugMode && !inSearch){
            System.out.println((blackToMove() ? "\nBlack is moving.\n" : "White is moving.\n") + notationToString());
            System.out.println("----------------------------------------");
            System.out.println("Before move: \n" + toString());
        }

        if(inSearch){
            int count = 0;
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
        if (blackToMove && isFriendly(current)) {

            // Check existing black queens to find which one has the same starting position
            for (int i = 0; i < blackQueens.length; i++) {
                if (blackQueens[i][0] == oldX && blackQueens[i][1] == oldY) {
                    blackQueens[i] = new int[]{newX, newY};
                    break;
                }
            }
        } else if (!blackToMove && isFriendly(current)){
            // Check existing white queens to find which one has the same starting position
            for (int i = 0; i < whiteQueens.length; i++) {
                if (whiteQueens[i][0] == oldX && whiteQueens[i][1] == oldY) {
                    whiteQueens[i] = new int[]{newX, newY};
                    break;
                }
            }        
        } 
        // else if (!inSearch) {
        //     throw new IllegalArgumentException("Illegal move! " + (blackToMove ? "Black" : "White") + " tried moving their opponent's queen");
        // }

        // Once a move is made, it is the other player's turn
        invertPlayer();
        
        if(debugMode && !inSearch){
            System.out.println("--------------------------------------\nAfter move: \n" + toString());
            System.out.println(notationToString());
        }
    }

    // Undo a move that was made during search.
    public void unmakeMove(Move move){
        
        // If move has already been played, then move.newPos contains the current position of the queen  
        int currentX = move.getNewPos().get(0);
        int currentY = move.getNewPos().get(1);

        // move.oldPos contains the location we must move it to
        int previousX = move.getOldPos().get(0);
        int previousY = move.getOldPos().get(1);

        // arrow same as normal, but this time we must empty the square at those coordinates.
        int arrowX = move.getArrowPos().get(0);
        int arrowY = move.getArrowPos().get(1);

        // Get value of the queen that is to be moved
        byte queen = gameBoard[currentX][currentY];

        // Update the board 
        gameBoard[currentX][currentY] = EMPTY;
        gameBoard[previousX][previousY] = queen;
        gameBoard[arrowX][arrowY] = EMPTY;

        // Update the queen that moved
        if (queen == BLACK) {

            // Check existing black queens to find which one has the same starting position
            for (int i = 0; i < blackQueens.length; i++) {
                if (blackQueens[i][0] == currentX && blackQueens[i][1] == currentY) {
                    blackQueens[i] = new int[]{previousX, previousY};
                    break;
                }
            }

        } else {
            // Check existing white queens to find which one has the same starting position
            for (int i = 0; i < whiteQueens.length; i++) {
                if (whiteQueens[i][0] == currentX && whiteQueens[i][1] == currentY) {
                    whiteQueens[i] = new int[]{previousX, previousY};
                    break;
                }
            }
        }

        invertPlayer();
    }

    private void invertPlayer() {
        this.blackToMove = !this.blackToMove;
    }

    private void printQueens() {
        System.out.println("Friendly queens: " + Arrays.deepToString(this.blackToMove ? blackQueens : whiteQueens));
        System.out.println("Enemy queens: " + Arrays.deepToString(this.blackToMove ? whiteQueens : blackQueens));
    }

    public Object clone(){

        Board clone = new Board();
        clone.blackToMove = this.blackToMove;

        // Iterate through the board:
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                clone.gameBoard[i][j] = this.gameBoard[i][j];
            }
        }

        for(int i = 0; i < this.blackQueens.length; i++){
            for(int j = 0; j < this.blackQueens[i].length; j++){
                clone.blackQueens[i][j] = this.blackQueens[i][j];
                clone.whiteQueens[i][j] = this.whiteQueens[i][j];
            }
        }

        return clone;
    }

    // Function used to check if a piece is friendly
    public boolean isFriendly(int color){
        return blackToMove ? color == BLACK : color == WHITE;
    }

    // Function to check if a square is empty
    public boolean isEmpty(int row, int col){
        return gameBoard[row][col] == EMPTY;
    }

    // Function used to temporarily move queen on board (only for move generation, internal queen positions not updated)
    // Warning: if used, must use it again to move the queen back.
    public boolean moveQueenOnGameBoard(int[] queen, int[] square){

        int oldX = queen[0], oldY = queen[1], newX = square[0], newY = square[1];

        if (!isEmpty(newX, newY) || isEmpty(oldX, oldY) || getPos(oldX, oldY) == ARROW) return false;
        
        byte color = getPos(oldX, oldY);
        this.gameBoard[oldX][oldY] = EMPTY;
        this.gameBoard[newX][newY] = color;  

        return true;
    }

    public int[][] getBlackQueens() {
        return this.blackQueens;
    }

    public int[][] getWhiteQueens() {
        return this.whiteQueens;
    }

    public byte getPos(int row, int col) {
        return gameBoard[row][col];
    }

    public boolean blackToMove() {
        return blackToMove;
    }

    //  Don't need this method anymore
    public String notationToString(){

        StringBuilder boardToString = new StringBuilder();
        StringBuilder whiteQueenPositions = new StringBuilder();
        StringBuilder blackQueenPositions = new StringBuilder();

        boardToString.append("");

        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        whiteQueenPositions.append("White queens: [");
        blackQueenPositions.append("Black queens: [");

        for (int x = BOARD_SIZE - 1; x >= 0; x--) { 
            // boardToString.append("[");
            for (int y = 0; y < BOARD_SIZE; y++) {    
                // boardToString.append(letters[y] + (x + 1) + (y == BOARD_SIZE-1 ? "": ", "));
                if (gameBoard[y][x] == WHITE) {
                    whiteQueenPositions.append(letters[y] + (x + 1) + " ");
                }
                else if(gameBoard[y][x] == BLACK){
                    blackQueenPositions.append(letters[y] + (x + 1) + " ");
                }
            }
            // boardToString.append("]\n");
        }

        whiteQueenPositions.append("]" + (blackToMove ? "" : "<--")); blackQueenPositions.append("]" + (blackToMove ? "<--" : ""));
        boardToString.append(whiteQueenPositions.toString() + "\n" + blackQueenPositions.toString());

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