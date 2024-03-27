package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Evaluator;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

public class Minimax {

    // private final int MAX_DEPTH = 15;

    private int numberOfMoves;

    // private Move bestMoveThisIteration;
    // private int bestEvalThisIteration;
    private Move bestMove;
    private int bestEval;

    private Board board;

    private int mode;
    private Evaluator evaluator;

    public boolean searchCancelled = false;

    // To count the number of leaf nodes generated in the search
    public int numStaticEvaluations = 0;
    public int depthReached = 0;

    public int movesMade = 0;
    public int movesUnmade = 0;

    private long timeRemaining;

    public Minimax(Board board, int numberOfMoves, int mode, long timeRemaining){
        this.board = board;
        this.evaluator = new Evaluator(board);
        this.numberOfMoves = numberOfMoves;
        this.mode = mode;
        this.timeRemaining = timeRemaining;
        this.bestMove = Move.nullMove();
        this.bestEval = 0;
    }

    /* 
    Does not work right now 
    public int iterativeDeepening(){

        bestEvalThisIteration = bestEval = 0;
		bestMoveThisIteration = bestMove = Move.nullMove();

        Board newBoard = (Board) this.board.clone();

        for(int depth = 1; depth <= MAX_DEPTH; depth++){

            this.board = newBoard;
            minimaxEvaluation(depth);
            System.out.println("Difference: " + (movesMade - movesUnmade));
            movesMade = 0; movesUnmade = 0;

            if(searchCancelled){
                depthReached = depth - 1;
                break;
            } else{
                bestMove = bestMoveThisIteration;
                bestEval = bestEvalThisIteration;
            }      
        }
        return bestEval;
    }
    */ 
    

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

        if(timeRemaining <= 500){
            searchCancelled = true;
            return 0;
        }

        if(depth == 0){
            numStaticEvaluations++;
            if(mode == 1){
                return evaluator.simpleEval();
            }
            return (mode == 2) ? evaluator.notSoSimpleEval(numberOfMoves) : evaluator.customEval();
        }

        ArrayList<Move> moves = MoveGenerator.getAllMoves(board);
        // If no moves available, the player has lost, i.e. return worst possible evaluation
        if (moves.size() == 0)
            return Integer.MIN_VALUE + plyFromRoot;
        
        for(Move move: moves){
            // Make move, find evaluation at depth = d-1, unmake move, then decide whether to prune.
            // ERROR: board is completely messed up after minimax returns. Probably an issue here.
            // Update: temporary fix - passing a clone of the board to instantiate minimax
            board.makeMove(move,false,true); 
            movesMade++;
            int eval = -(minimaxEvaluation(depth - 1, plyFromRoot + 1, -beta, -alpha));     
            board.unmakeMove(move);
            movesUnmade++;

            if(eval >= alpha){
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
