package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Evaluator;
import ubc.GameState.Move;

public class MonteCarlo {	
	long allowedTime;
	Node root;
	double ec;
    Evaluator evaluator = new Evaluator(root.getBoard());
	
	public MonteCarlo(Node root, long allowedTime, double ec){
		this.root = root;
		this.allowedTime = allowedTime;
		this.ec = ec;
	}
	
	// performs an MCTS from the current root and returns the best action
	public Move MCTS() {

		long currentTime = System.currentTimeMillis();
		int numEvaluations = 0;
        
		for(long startTime = System.currentTimeMillis(); currentTime - startTime < allowedTime; currentTime = System.currentTimeMillis()) {
			Node leaf = traverse(root);
			double result;
			if(leaf.isTerminalState()) {
				result = 1;
			} else {
				leaf = leaf.getRandomChild();
				result = evaluate(leaf);
			}
			numEvaluations++;
			backpropogate(leaf, result);
		}
		System.out.println(numEvaluations + " nodes were evaluated.");

		// returns an action based on child with highest winrate
		Move bestAction = null;
		double bestWinrate = Double.MIN_VALUE;
		for (Node child : root.getChildren()) {
			double winrate = 0;
			double Q = child.getQ();
			double N = child.getN();
			if (N != 0) {
				winrate = Q / N;
			}
			if (winrate > bestWinrate) {
				bestWinrate = winrate;
				bestAction = child.getMove();
			}
		}
        System.out.println("Win percentage: " + bestWinrate);
		return bestAction;
	}

    // returns the child node with the highest UCB 
	public Node traverse(Node node) {
        
		if (!node.hasUnexpandedChildren() && node.hasExpandedChildren()) {
			
			double maxUCB = Double.MIN_VALUE; 
			Node bestChild = null;
			for (Node n : node.getChildren()) {
				double currentUCB = n.getUCBEvaluation(ec);
				if (currentUCB > maxUCB) {
					maxUCB = currentUCB;
					bestChild = n;
				}
			}
			// traverse recursively
			return traverse(bestChild);
		}
		return node;
	}
	
	public double evaluate(Node node) {
		double eval = evaluator.notSoSimpleEval();
		double result = 1 / (1 + Math.exp(-eval/5));
		
		if(node.getBoard().blackToMove()) {
			return result;
		} else {
			return 1 - result;
		}
	}
	
	public void backpropogate(Node leaf, double result) {

		leaf.incrementN(); 
        leaf.incrementQ(result);

		if(leaf.getParent() != null) {
			backpropogate(leaf.getParent(), 1 - result);
		}
	}
	
	public void moveRootAfterMove(Move move) {

		this.root.expandChildren();
		for(Node n: root.getChildren()) {
			if(n.getMove().equals(move)) {
				root = n;
				root.setParent(null);
				break;
			}
		}
	}
	
}
