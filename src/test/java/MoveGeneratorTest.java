import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

public class MoveGeneratorTest {
    public static void main(String[] args){
        byte[][] sampleBoard = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0},
            {0,0,0,2,1,1,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
        };
    
        Board board = new Board(sampleBoard);

        ArrayList<ArrayList<Integer>> moves = MoveGenerator.getChessKingMovesFromSquare(new int[]{4,4}, board);
        System.out.println(moves.size());

        for(ArrayList<Integer> move: moves){
            int i = move.get(0);
            int j = move.get(1);
            sampleBoard[i][j] = 3;
            System.out.println(move);
        }

        Board newBoard = new Board(sampleBoard);
        System.out.println(newBoard);
    }
}
