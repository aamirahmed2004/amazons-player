package ubc.GameTree;

import java.util.ArrayList;

import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

//Each node has a board, a move, and an evaluation after that move is made on the board.
public class Node {

    private Board board;
    private Move move;
    private int evaluation;

    private Node parent;                    
    private Node root;      
    private ArrayList<Move> possibleMoves;  //possible moves from this position
    private ArrayList<Node> children;       
    
    private boolean isTerminalState = false;

    public Node(Board previousBoard, Move move, Node parent, Node root) {

        this.board = Board.getNewBoard(previousBoard, move);
        int currentPlayer = this.board.isBlack() ? Board.BLACK : Board.WHITE;

        this.root = root;
        this.move = move;
        
        this.parent = parent;

        //generate all possible moves for current player
        this.possibleMoves = MoveGenerator.getAllMoves(this.board, currentPlayer); 
    
        if(possibleMoves.size() == 0){

            this.isTerminalState = true;
            
            if(currentPlayer == Board.WHITE)        
                this.evaluation = Integer.MAX_VALUE;    //if white to move and no moves left, then black wins (i.e. highest possible evaluation)
            else this.evaluation = Integer.MIN_VALUE;   //if black to move and no moves left, then white wins (i.e. lowest possible evaluation)

        } else {

            this.children = new ArrayList<Node>();

            for(Move possibleMove: possibleMoves){
                addChild(new Node(this.board,possibleMove,this,this.root));
            }
        }
            
    }

    // Constructor for root node
    public Node(Board board, int color){ 

        this.board = board;
        this.possibleMoves = MoveGenerator.getAllMoves(board, board.isBlack() ? Board.BLACK : Board.WHITE);
        this.root = this;
        this.parent = null;
        this.move = null;

        if(possibleMoves.size() == 0){

            this.isTerminalState = true;

            if(!board.isBlack())        
                this.evaluation = Integer.MAX_VALUE;    //if white to move and no moves left, then black wins (i.e. highest possible evaluation)
            else this.evaluation = Integer.MIN_VALUE;   //if black to move and no moves left, then white wins (i.e. lowest possible evaluation)

        } else{
            this.children = new ArrayList<Node>();
            for(Move possibleMove: possibleMoves){
                addChild(new Node(this.board,possibleMove,this,this.root));     //currently adds children of children of children... without stopping. Add depth attribute?
            }
        }
        
    }

    public boolean isTerminalState(){
        return isTerminalState;
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

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public void setEvaluation(int eval){
        this.evaluation = eval;
    }

    public int getValue(){
        return evaluation;
    }

}
