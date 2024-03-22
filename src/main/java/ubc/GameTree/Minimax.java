package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Heuristics;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

public class Minimax {

    private Move bestMove;
    private int bestEval;
    private boolean abortSearch;    
    private Board board;

    // To count the number of leaf nodes generated in the search
    public int numStaticEvaluations = 0;

    public Minimax(Board board){
        this.board = board;
        this.bestEval = 0;
        this.bestMove = Move.nullMove();
    }

    /*
     * Using a variant of minimax called NegaMax modified by alpha-beta pruning
     * Algorithm from: https://www.ngorski.com/data/uploads/aigametheory/efficient-implementation-of-combinatoric-ai.pdf
     * 
     * Returns the evaluation for a position starting from the given board position.
     * Arguments:
     *  board is the starting position.
     *  depth is the maximum depth to go until before static evaluation of the position.
     *  plyFromRoot is the distance from the root node, i.e. number of moves that have been made since the given board position.
     */
    public int minimaxEvaluation(int depth, int plyFromRoot, int alpha, int beta){

        if(depth == 0){
            numStaticEvaluations++;
            return Heuristics.simpleEval(board);
        }

        ArrayList<Move> moves = MoveGenerator.getAllMoves(board);
        // If no moves available, the player has lost, i.e. return worst possible evaluation
        if (moves.size() == 0)
            return Integer.MIN_VALUE + plyFromRoot;
        
        for(Move move: moves){
            // Make move, find evaluation at depth = d-1, unmake move, then decide whether to prune.
            // ERROR: board is completely messed up after minimax returns. Probably an issue here.
            board.makeMove(move,true);
            int eval = -(minimaxEvaluation(depth - 1, plyFromRoot + 1, -beta, -alpha));     
            board.unmakeMove(move);

            if(eval > alpha){
                alpha = eval;

                if(plyFromRoot == 0){
                    bestMove = move;
                    bestEval = eval;
                }
            }
            // Pruning condition
            if (alpha >= beta)
                break;
        }

        return alpha;
    }

    public int minimaxEvaluation(int depth){
        return minimaxEvaluation(depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Move getBestMove(){
        return bestMove;
    }

    public int getEvaluation(){
        return bestEval;
    }
}
