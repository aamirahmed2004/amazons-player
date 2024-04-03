
package ubc.GamePlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sfs2x.client.entities.Room;
import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;
import ubc.GameTree.Minimax;
import ubc.GameTree.Timer;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class Jarvis_v3 extends GamePlayer{

    public final int EMPTY = 0, WHITE = 1, BLACK = 2, ARROW = 3, BOARD_SIZE = 10, MAX_DEPTH = 25;

    private GameClient gameClient; 
    private BaseGameGUI gamegui;

    private String userName;
    private String passwd;
	
    private Board board;
	
    private int player;
    private int moveCount;
    private int evalMode = 1;

    private Timer timer;

    private int roomNumber;
    private boolean debugMode;

    private StringBuilder gameRecord = new StringBuilder();
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public Jarvis_v3(String userName, String passwd, int roomNumber, int evalMode, long timeLimit, boolean debugMode) {
    	this.userName = userName;
    	this.passwd = passwd;
    	this.roomNumber = roomNumber;
        this.evalMode = evalMode;
        this.debugMode = debugMode;
        this.timer = new Timer(timeLimit);
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }


    @Override
    public void onLogin() {
    	// System.out.println("Congratualations!!! "
    	// 		+ "I am called because the server indicated that the login is successfully");
    	// System.out.println("The next step is to find a room and join it: "
    	// 		+ "the gameClient instance created in my constructor knows how!"); 		

		List<Room> rooms = gameClient.getRoomList();

        // if(rooms.isEmpty()){
        //     System.out.println("No rooms available right now!");
        // }

        String roomToJoin = rooms.get(roomNumber).getName();
        System.out.println("Joining Room: " + roomToJoin);
        gameClient.joinRoom(roomToJoin);

        userName = gameClient.getUserName();
        if(gamegui != null){
            gamegui.setRoomInformation(gameClient.getRoomList());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		System.out.println("AI Player received game message - Type : "+ messageType + ", Details: " + msgDetails);        

        if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {

            getGameGUI().setGameState((ArrayList<Integer>) msgDetails.get("game-state"));
            // System.out.println("Game State: " + msgDetails.get("game-state"));

        } else if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {

            getGameGUI().updateGameState(msgDetails);
            moveCount++;

            Move opponentsMove = new Move(msgDetails);
            System.out.println("Opponent's Move: " + opponentsMove.toString());
            gameRecord.append(opponentsMove.toString() + " ");

            this.board.makeMove(opponentsMove, true, false);

            if(player == WHITE && moveCount == 0)
                makeRandomMove();
            else makeAIMove();
            moveCount++;

        } else if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            board = new Board(true); board.debugMode = this.debugMode;

            if(this.userName().equals(msgDetails.get("player-black")))
                this.player = BLACK;
            else if(this.userName().equals(msgDetails.get("player-white")))
                this.player = WHITE;
            
            System.out.println("Game Start: Black Played by " + msgDetails.get("player-black"));
            System.out.println("Game Start: White Played by " + msgDetails.get("player-white"));

            System.out.println("Timer Started on Black");

            if(this.player == BLACK){
                makeOpeningMove();
                // makeRandomMove();
                // makeSampleMove();
            }
            moveCount++;
        }

    	return true;   	
    }
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}

    private void makeAIMove(){

        timer.resetTimer();

        int depth = 0;
        timer.startTimer();
        
        Board clone;
        Minimax minimax; 
        System.out.println("Starting evaluation!");
        int evaluation = 0; 
        Move bestMove = Move.nullMove(); 

        do{
            depth++;
            clone = (Board) this.board.clone();
            minimax = new Minimax(clone, moveCount, evalMode, timer.getRemainingTime());
            minimax.minimaxEvaluation(depth);

            if(minimax.searchCancelled && bestMove.isNull()){
                System.out.println("Time elapsed: " + timer.getTimeElapsed() + "\nEvaluation cancelled, making random move");
                makeRandomMove();
                break;
            }

            if(!minimax.searchCancelled) {
                bestMove = minimax.getBestMove();
                evaluation = minimax.getEvaluation();
            }

        } while(!minimax.searchCancelled && depth < MAX_DEPTH);
        
        if(bestMove.isNull()){
            System.out.println("----------------------------------");
            System.out.println("Who would win? v3."+ evalMode + " or Deep Blue (Deep Blue)");
            System.out.println("----------------------------------");
            return;
        }

        System.out.println("\n\nVersion 3." + evalMode + ": \nCurrent evaluation: " + evaluation + "\nDepth: " + depth + "\nNumber of static evaluations: " + minimax.numStaticEvaluations);
        System.out.println("Best move found: " + bestMove.toString());
        gameRecord.append(bestMove.toString() + " ");

        this.board.makeMove(bestMove);

        Move moveForServer = bestMove.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);

    }

    private void makeOpeningMove(){

        Random random = new Random();
        double randomNumber = random.nextDouble(); // Generate a random number between 0 and 1
        Move move = Move.nullMove();
        
        if (randomNumber < 0.2) {
            move = new Move("g10-g5/i5");
        } else if (randomNumber < 0.4) {
            move = new Move("g10-g3/i5");
        } else if (randomNumber < 0.6) {
            move = new Move("g10-g5/i5");
        } else if (randomNumber < 0.8) {
            move = new Move("d10-d3/b5");
        } else {
            move = new Move("d10-g2/e2");
        }

        System.out.println("Opening Move: " + move.toString());
        gameRecord.append(move.toString() + " ");

		this.board.makeMove(move);

        Move moveForServer = move.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);
    }

    private void makeRandomMove() {

		ArrayList<Move> moves = MoveGenerator.getAllMoves(this.board);

        if(moves.size() == 0){
            System.out.println("----------------------------------");
            System.out.println("Nah I'd win (we lost)");
            System.out.println("----------------------------------");
            return;
        }

		Move randomMove = moves.get((int) (Math.random() * moves.size()));
        System.out.println("Random Move: " + randomMove.toString());
        gameRecord.append(randomMove.toString() + " ");

		this.board.makeMove(randomMove);

        Move moveForServer = randomMove.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);
    }

    @SuppressWarnings("unused")
    private void makeSampleMove() {
        Move move1 = new Move(0,6,3,3,4,4);
        Move move2 = new Move(3,9,3,3,4,4);
        this.board.makeMove(move1);
        this.board.unmakeMove(move1);
        this.board.makeMove(move2);

        Move moveForServer = move2.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        gameClient.sendMoveMessage(currentPos, newPos, arrowPos);
        gamegui.updateGameState(currentPos, newPos, arrowPos);
    }

    private boolean wasGameValid(){
        
        int startingPlayer = player;
        Board board = (player == Board.BLACK) ? new Board(true) : new Board(false);
        String[] moves = gameRecord.toString().split(" ");
        boolean currentPlayerIsBlack = (player == Board.BLACK);

        for(String moveString: moves){
            Move play = new Move(moveString);  
            ArrayList<Move> possibleMoves = MoveGenerator.getAllMoves(board, startingPlayer);
            if(!possibleMoves.contains(play)){
                return false;
            }
            currentPlayerIsBlack = !currentPlayerIsBlack;
        }

        return true;
    }
 
}//end of class
