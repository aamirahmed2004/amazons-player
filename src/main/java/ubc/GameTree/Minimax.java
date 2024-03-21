package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Heuristics;
import ubc.GameState.Move;

public class Minimax {

    static boolean debug = false;

    // We assume that Black wants to maximize their score and White wants to minimize.
    public static Move minimaxTree(Board board, int player, int depth) {
        
        Node rootNode = new Node(board, player);
        ArrayList<Node> children = rootNode.getChildren();
        if(children == null){
            return null;
        }
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Move bestMove = null;
        int finalEval = 0;
        

        System.out.println("Starting minimax search!");

        if(player == Board.WHITE){
            System.out.println("Evaluating minimizing move...");
            int minEval = Integer.MAX_VALUE;
            for (Node childNode: rootNode.getChildren()){
                int eval = Minimax.maxValue(childNode, depth-1, alpha, beta);
                childNode.setEvaluation(eval);
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = childNode.getMove();
                    finalEval = eval;
                }
                beta = Math.min(eval,beta);
            }
        }

        else{
            System.out.println("Evaluating maximizing move...");
            int maxEval = Integer.MIN_VALUE;
            for (Node childNode: children){
                int eval = Minimax.minValue(childNode, depth-1, alpha, beta);
                childNode.setEvaluation(eval);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = childNode.getMove();
                    finalEval = eval;
                }
                alpha = Math.max(alpha, eval);
            }
   
        }
        System.out.println("Best move evaluation: " + finalEval);       
        return bestMove;
    }

    public static int maxValue(Node node, int depth, int alpha, int beta) {

        int count = 0;

        if(node.isTerminalState())
            return node.getValue();

        else if (depth == 0) {
            if(debug)
                System.out.println("Reached max depth in maxValue");
            return Heuristics.simpleEval(node.getBoard());  
        }

		else {
            if(debug)
                System.out.println("In maxValue at depth: " + depth);
            int eval = Integer.MIN_VALUE;
            int newAlpha = alpha;

            for(Node child: node.getChildren()){
                count++;
                if(count == 100){
                    System.out.println("hello");
                }
                eval = Math.max(eval, minValue(child, depth-1, newAlpha, beta));
                child.setEvaluation(eval);

                if(eval >= beta){
                    child.setEvaluation(eval);
                    return eval;
                }
                    
                newAlpha = Math.max(eval,newAlpha);
            }
            return eval;
        }
	}

	public static int minValue(Node node, int depth, int alpha, int beta) {

        int count = 0;

        if(node.isTerminalState())
            return node.getValue();

        else if (depth == 0) {
            if(debug)
                System.out.println("Reached max depth in minValue");
            return Heuristics.simpleEval(node.getBoard());  
        }

		else {
            if(debug)
                System.out.println("In minValue at depth: " + depth);
            int eval = Integer.MAX_VALUE;
            int newBeta = beta;

            for(Node child: node.getChildren()){
                count++;
                if(count == 100){
                    System.out.println("hello");
                }
                eval = Math.min(eval, Minimax.maxValue(child, depth - 1, alpha, newBeta));
                child.setEvaluation(eval);

                if(eval <= alpha){    
                    return eval;
                }       
                newBeta = Math.min(eval,newBeta);
            }
            return eval;
        }
	}

    /* For reference, delete later
    public static Move minimax(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {

        if (depth == 0 || node.getChildren().isEmpty()) {
            return Heuristics.simpleEval(node.getBoard(), maximizingPlayer);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Node childNode: node.getChildren()){
                int eval = minimax(childNode, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Node childNode: node.getChildren()) {
                int eval = minimax(childNode, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }   
    */  
}
