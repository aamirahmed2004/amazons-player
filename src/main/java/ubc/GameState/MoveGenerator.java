package ubc.GameState;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveGenerator {

    // Returns a list of Moves, where each Move contains the old position, new position, and arrow position, as separate arraylists.
    public static ArrayList<Move> getAllMoves(Board board, int color) {
	
		ArrayList<Move> moveList = new ArrayList<Move>();

        int[][] queens = (board.isFriendly(color)) ? board.getFriendlyQueens() : board.getEnemyQueens();

        // For every queen (i.e. runs 4 times), find all the squares it can be moved to
		for (int i = 0; i < queens.length; i++) {

			int[] currentQueen = new int[]{queens[i][0], queens[i][1]};
			ArrayList<ArrayList<Integer>> possibleQueenMoves = getQueenMovesFromSquare(currentQueen, board);

            // For every square a queen can be moved to (i.e. runs on average ~20 times), find all the squares an arrow can shot to  
			for (int j = 0; j < possibleQueenMoves.size(); j++) {

				int[] possibleQueenPosition = new int[]{possibleQueenMoves.get(j).get(0), possibleQueenMoves.get(j).get(1)}; 

                // Temporarily move the queen on the board (but not internally in the queens array), to find arrow moves from new queen position, then move it back
				board.moveQueenOnGameBoard(currentQueen, possibleQueenPosition);    
				ArrayList<ArrayList<Integer>> possibleArrowPositions = getArrowMovesFromSquare(possibleQueenPosition, board);
				board.moveQueenOnGameBoard(possibleQueenPosition, currentQueen);	// move queen back

                // For every square that the queen can shoot an arrow to (i.e. runs on average ~20 times), add the move to moveList
				for (int k = 0; k < possibleArrowPositions.size(); k++) {

                    int[] arrowPos = new int[]{possibleArrowPositions.get(k).get(0), possibleArrowPositions.get(k).get(1)};

					// Use Move constructor that takes in: oldX, oldY, newX, newY, arrowX, arrowY
					moveList.add(new Move(currentQueen[0], currentQueen[1], possibleQueenPosition[0], possibleQueenPosition[1], arrowPos[0], arrowPos[1]));
				}
			}
		}
		return moveList;    
	}

    /*
    * Chess queen move: free movement in all 8 directions around a square, assuming the path is clear. 
    * We find chess queen moves as an intermediate step, since finding all Amazons moves is equivalent to 
    *  finding all pairs of two consecutive chess queen moves (one for the Amazon, one for the arrow).
    * 
    * Returns a list of all the possible chess queen moves that can be made from a given square.
    * Output moveList is in the following format: [[newX1, newY1], [newX2, newY2], ...]
    */

    private static ArrayList<ArrayList<Integer>> getQueenMovesFromSquare(int[] square, Board board) {
        return getChessQueenMovesFromSquare(square, board);
    }

    private static ArrayList<ArrayList<Integer>> getArrowMovesFromSquare(int[] square, Board board) {
        return getChessQueenMovesFromSquare(square, board);
    }

    public static ArrayList<ArrayList<Integer>> getChessQueenMovesFromSquare(int[] square, Board board) {

        ArrayList<ArrayList<Integer>> moveList = new ArrayList<ArrayList<Integer>>();
		int row = square[0], col = square[1];

        /*
        * Algorithm: 
        * 
        * for every move along a particular direction:
        *    if next square is occupied, stop
        *    else, add square to list
        *
        * repeat for all 8 directions
        */

        // Right
		for (int i = 1; row + i < Board.BOARD_SIZE; i++) {    
			if (!board.isEmpty(row + i, col)) break;
            moveList.add(new ArrayList<Integer>(Arrays.asList(row + i, col)));
		}

        // Left
		for (int i = 1; row - i >= 0; i++) {
			if (!board.isEmpty(row - i, col)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row - i, col)));
		}

        // Up
		for (int i = 1; col + i < Board.BOARD_SIZE; i++) {
			if (!board.isEmpty(row, col + i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row, col + i)));
		}

        // Down
		for (int i = 1; col - i >= 0; i++) {
			if (!board.isEmpty(row, col - i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row, col - i)));
		}

        // Up right
        for (int i = 1; row + i < Board.BOARD_SIZE && col + i < Board.BOARD_SIZE; i++) {
			if (!board.isEmpty(row + i, col + i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + i, col + i)));
		}

        // Down left
        for (int i = 1; row - i >= 0 && col - i >= 0; i++) {
			if (!board.isEmpty(row - i, col - i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row - i, col - i)));
		}

        // Down right
		for (int i = 1; row + i < Board.BOARD_SIZE && col - i >= 0; i++) {
			if (!board.isEmpty(row + i, col - i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + i, col - i)));
		}

		// Up left
		for (int i = 1; row - i >= 0 && col + i < Board.BOARD_SIZE; i++) {
			if (!board.isEmpty(row - i, col + i)) break;
			moveList.add(new ArrayList<Integer>(Arrays.asList(row - i, col + i)));
		}

		return moveList;
	}

	public static ArrayList<ArrayList<Integer>> getChessKingMovesFromSquare(int[] square, Board board) {

        ArrayList<ArrayList<Integer>> moveList = new ArrayList<ArrayList<Integer>>();
		int row = square[0], col = square[1];

        /*
        * Algorithm: 
        * 
        * for each of the 8 directions
        *    if next square in that direction is empty
        *    	add square to list
        */

        // Right
		if (row + 1 < Board.BOARD_SIZE && board.isEmpty(row + 1, col)) 
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + 1, col)));

        // Left
		if (row - 1 >= 0 && board.isEmpty(row - 1, col)) 
			moveList.add(new ArrayList<Integer>(Arrays.asList(row - 1, col)));

        // Up
		if (col + 1 < Board.BOARD_SIZE && board.isEmpty(row, col + 1)) 
			moveList.add(new ArrayList<Integer>(Arrays.asList(row, col + 1)));

        // Down
		if (col - 1 >= 0 && board.isEmpty(row, col - 1)) 
			moveList.add(new ArrayList<Integer>(Arrays.asList(row, col - 1)));

        // Up right
        if (row + 1 < Board.BOARD_SIZE && col + 1 < Board.BOARD_SIZE && board.isEmpty(row + 1, col + 1))
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + 1, col + 1)));


        // Down left
        if (row - 1 >= 0 && col - 1 >= 0 && board.isEmpty(row - 1, col - 1))
			moveList.add(new ArrayList<Integer>(Arrays.asList(row - 1, col - 1)));

        // Down right
		if (row + 1 < Board.BOARD_SIZE && col - 1 >= 0 && board.isEmpty(row + 1, col - 1))
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + 1, col - 1)));

		// Up left
		if (row - 1 >= 0 && col + 1 < Board.BOARD_SIZE && board.isEmpty(row + 1, col - 1))
			moveList.add(new ArrayList<Integer>(Arrays.asList(row + 1, col - 1)));

		return moveList;
	}
}
