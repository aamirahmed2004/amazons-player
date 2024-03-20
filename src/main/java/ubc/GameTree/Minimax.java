package ubc.GameTree;

import ubc.GameState.Board;
import ubc.GameState.Heuristics;
import ubc.GameState.Move;

public class Minimax {

    // We assume that Black wants to maximize their score and White wants to minimize.
    public static Move minimaxTree(Board board, int player, int depth) {

        System.out.println("Starting minimax search!");
        
        Node rootNode = new Node(board, player);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Move bestMove = null;

        if(player == Board.BLACK){
            System.out.println("Evaluating maximizing move...");
            int minEval = Integer.MAX_VALUE;
            for (Node childNode: rootNode.getChildren()){
                int eval = Minimax.minValue(childNode, depth - 1, alpha, beta);
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = childNode.getMove();
                }
                beta = Math.min(eval,beta);
            }
        }

        else{
            int maxEval = Integer.MIN_VALUE;
            System.out.println("Evaluating minimizing move...");
            for (Node childNode: rootNode.getChildren()){
                int eval = Minimax.maxValue(childNode, depth - 1, alpha, beta);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = childNode.getMove();
                }
                alpha = Math.max(alpha, eval);
            }
        }
            
        return bestMove;
    }

    public static int maxValue(Node node, int depth, int alpha, int beta) {

        if(node.isTerminalState())
            return node.getValue();

        else if (depth == 0) 
			return Heuristics.simpleEval(node.getBoard());  

		else {
            int eval = Integer.MIN_VALUE;
            int newAlpha = alpha;

            for(Node child: node.getChildren()){

                eval = Math.max(eval, minValue(child, depth-1, newAlpha, beta));

                if(eval >= beta)
                    return eval;
                
                newAlpha = Math.max(eval,newAlpha);
            }
            return eval;
        }
	}

	public static int minValue(Node node, int depth, int alpha, int beta) {

        if(node.isTerminalState())
            return node.getValue();

        else if (depth == 0) 
			return Heuristics.simpleEval(node.getBoard());  

		else {
            int eval = Integer.MAX_VALUE;
            int newBeta = beta;

            for(Node child: node.getChildren()){

                eval = Math.min(eval, Minimax.maxValue(child, depth - 1, alpha, newBeta));

                if(eval <= alpha)
                    return eval;
                
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
