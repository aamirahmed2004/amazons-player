import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.MoveGenerator;
import ubc.GameTree.Minimax;

public class EvaluatorTest {
    public static void main(String[] args){
        byte[][] sampleBoard = {
            {2,3,0,0,0,0,0,0,3,1},
            {2,3,0,0,0,0,0,0,3,1},
            {2,3,0,0,0,0,0,0,3,1},
            {2,3,0,0,0,0,0,0,3,3},
            {3,3,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1}
        };
    
        Board board = new Board(sampleBoard);

        Minimax minimax = new Minimax(board, 10, 3, 10000);
        int evaluation = minimax.minimaxEvaluation(0);
        System.out.println(evaluation);
    }
}
