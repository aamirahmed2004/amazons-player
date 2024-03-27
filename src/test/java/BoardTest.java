import java.util.ArrayList;
import java.util.Arrays;

import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

public class BoardTest {
    public static void main(String[] args){
        // byte[][] sampleBoard = {
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,1,0,0,0,0},
        //     {0,0,0,2,1,1,0,0,0,0},
        //     {0,0,0,0,0,1,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0},
        //     {0,0,0,0,0,0,0,0,0,0}
        // };
    
        Board board = new Board(false);
        System.out.println(board.toString());
        System.out.println(Arrays.deepToString(board.getBlackQueens()));
        System.out.println(Arrays.deepToString(board.getWhiteQueens()));

        // (0,6) is a black queen, (0,3) is a white queen, both can reach (3,3)
        Move move1 = new Move(0,6,3,3,4,4);
        Move move2 = new Move(3,9,3,3,4,4);
        board.makeMove(move1,true,false);
        System.out.println(board.toString());
        // board.unmakeMove(move1);
        // board.makeMove(move2);


        // ArrayList<ArrayList<Integer>> moves = MoveGenerator.getChessKingMovesFromSquare(new int[]{4,4}, board);
        // System.out.println(moves.size());

        // for(ArrayList<Integer> move: moves){
        //     int i = move.get(0);
        //     int j = move.get(1);
        //     sampleBoard[i][j] = 3;
        //     System.out.println(move);
        // }

        // Board newBoard = new Board(sampleBoard);
        // System.out.println(newBoard);
    }
}       
