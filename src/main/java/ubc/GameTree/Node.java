package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

//Each node has a board, a move, and an evaluation after that move is made on the board.
public class Node {

    private Board board;
    private Move move;
    private int player;

    private double Q;
	private int N;

    private Node parent;                    
    private ArrayList<Move> possibleMoves = null;  //possible moves from this position
    private ArrayList<Node> children = null;       

    private boolean expanded = false;

    // Constructor for generating root node, parent must be null.
    public Node(Board board) {
        this.board = board;
        this.player = board.blackToMove() ? Board.BLACK : Board.WHITE;
        this.parent = null;
        this.N = 0;
        this.Q = 0;
        this.move = null;
        this.expanded = false;
    }

    // Constructor for all other nodes, given parent and move that led to this node.
    public Node(Board board, Node parent, Move move) { 

        this.board = board.getNewBoard(move);
        this.player = board.blackToMove() ? Board.BLACK : Board.WHITE;
        this.parent = parent;
        this.move = move;
        this.N = 0;
        this.Q = 0;

        this.children = new ArrayList<>();
    }

    public Node addChild(Move move){
        Node child = new Node(board, this, move);
        children.add(child);
        return child;
    }

    public void expandChildren(){
        if(possibleMoves == null){
            possibleMoves = MoveGenerator.getAllMoves(board, player);
        }
        for(Move move: possibleMoves){
            addChild(move);
        }
        expanded = true;
    }

    public boolean isExpanded(){
        return expanded;
    }

    public Node getRandomChild(){

        if(children == null)
            expandChildren();
        
        int randomIndex = (int) (Math.random() * possibleMoves.size());
        Move randomMove = possibleMoves.get(randomIndex);
        possibleMoves.remove(randomIndex);
        
        // We have found and exhausted all possible moves from this position
        if(this.possibleMoves.isEmpty())
            this.expanded = true;
        
        return addChild(randomMove);
    }

    public double getUCBEvaluation(double c) {
        if (N == 0) 
            return Double.MAX_VALUE;
        else
            return (Q / N) + c * Math.sqrt(Math.log(parent.N) / this.N);
    }

    public boolean isTerminalState(){
        if(possibleMoves == null){
            possibleMoves = MoveGenerator.getAllMoves(board, player);
        }
        return possibleMoves.isEmpty() && children.isEmpty();
    }

    public boolean hasUnexpandedChildren() {
        if(possibleMoves == null){
            possibleMoves = MoveGenerator.getAllMoves(board, player);
        }
        return !this.possibleMoves.isEmpty();
    }
    
    public boolean hasExpandedChildren() {
        return !this.children.isEmpty();
    }

    public Board getBoard() {
        return board;
    }

    public Move getMove(){
        return move;
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public int getN() {
        return N;
    }

    public double getQ(){
        return Q;
    }

    public void incrementN(){
        N++;
    }

    public void incrementQ(double value){
        Q += value;
    }
}