import java.util.ArrayList;
import java.util.Arrays;

import ubc.GameState.Board;
import ubc.GameState.MoveGenerator;
import ubc.GameTree.Minimax;

public class EvaluatorTest {
    public static void main(String[] args){
        byte[][] sampleBoard = {
            {0,0,0,0,0,0,0,0,3,0},
            {0,0,0,0,3,0,0,3,0,0},
            {0,0,0,1,0,2,0,0,0,0},
            {0,0,3,3,0,2,0,0,0,0},
            {0,0,0,0,3,0,0,0,3,0},
            {0,3,2,3,0,3,3,3,3,3},
            {0,0,0,0,0,3,1,1,3,0},
            {0,0,0,1,3,3,0,3,0,0},
            {0,3,2,0,0,3,0,0,0,0},
            {0,0,0,0,0,3,0,0,0,0}
        };
    
        Board board = new Board(sampleBoard);

        byte[][] mobilities = new byte[Board.BOARD_SIZE][Board.BOARD_SIZE];

        for(int i = 0; i < Board.BOARD_SIZE; i++){
            for(int j = 0; j < Board.BOARD_SIZE; j++){
                if(board.isEmpty(i, j)){
                    int[] square = new int[]{i,j};
                    mobilities[i][j] = (byte) MoveGenerator.getChessKingMovesFromSquare(square, board).size();
                }
                if(board.getPos(i,j) == 3){
                    mobilities[i][j] = -1;
                }    
            }
        }

        System.out.println(Arrays.deepToString(mobilities).replace("], ", "]\n").replace("[[", "[").replace("]]", "]").replace("-1", "X"));

        int[][] queens = board.getWhiteQueens();
        
        for(int[] queen: queens){

            int row = queen[0], col = queen[1]; 
            double queenMobility = 0;

            // Right
            for (int i = 1; row + i < Board.BOARD_SIZE; i++) {    
                if (!board.isEmpty(row + i, col)) break;
                byte numberOfReachableSquares = mobilities[row+i][col];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Left
            for (int i = 1; row - i >= 0; i++) {
                if (!board.isEmpty(row - i, col)) break;
                byte numberOfReachableSquares = mobilities[row-i][col];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up
            for (int i = 1; col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row, col + i)) break;
                byte numberOfReachableSquares = mobilities[row][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down
            for (int i = 1; col - i >= 0; i++) {
                if (!board.isEmpty(row, col - i)) break;
                byte numberOfReachableSquares = mobilities[row][col-i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up right
            for (int i = 1; row + i < Board.BOARD_SIZE && col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row + i, col + i)) break;
                byte numberOfReachableSquares = mobilities[row+i][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down left
            for (int i = 1; row - i >= 0 && col - i >= 0; i++) {
                if (!board.isEmpty(row - i, col - i)) break;
                byte numberOfReachableSquares = mobilities[row-i][col-i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down right
            for (int i = 1; row + i < Board.BOARD_SIZE && col - i >= 0; i++) {
                if (!board.isEmpty(row + i, col - i)) break;
                byte numberOfReachableSquares = mobilities[row+i][col-1];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up left
            for (int i = 1; row - i >= 0 && col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row - i, col + i)) break;
                byte numberOfReachableSquares = mobilities[row-i][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }
            System.out.println("Queen: " + Arrays.toString(queen) + "\nMobility: " + queenMobility);
            mobilities[row][col] = (byte) queenMobility;
        }
        System.out.println(Arrays.deepToString(mobilities).replace("], ", "]\n").replace("[[", "[").replace("]]", "]").replace("-1", "X"));
    }
}
