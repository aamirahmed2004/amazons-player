package ubc.GamePlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sfs2x.client.entities.Room;
import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameTree.MonteCarlo;
import ubc.GameTree.Node;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * 
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 *
 */
public class Gambler_v2 extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	MonteCarlo MCTree;
	public final int WHITE = 1, BLACK = 2;
	private int player;
	private long maxTime;
	private int roomNumber;


	/**
	 * Any name and passwd
	 * 
	 * @param userName
	 * @param passwd
	 */
	public Gambler_v2(String userName, String passwd, long maxTime, int roomNumber) {
		this.userName = userName;
		this.passwd = passwd;
		this.maxTime = maxTime;
		this.roomNumber = roomNumber;
		// To make a GUI-based player, create an instance of BaseGameGUI
		// and implement the method getGameGUI() accordingly
		this.gamegui = new BaseGameGUI(this);
	}

	@Override
	public void onLogin() {

		List<Room> rooms = gameClient.getRoomList();

        // if(rooms.isEmpty()){
        //     System.out.println("No rooms available right now!");
        // }

        String roomToJoin = rooms.get(roomNumber).getName();

        System.out.println("Joining Room: " + roomToJoin);
        gameClient.joinRoom(roomToJoin);
		userName = gameClient.getUserName();
		if (gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		// This method will be called by the GameClient when it receives a game-related
		// message
		// from the server.

		// For a detailed description of the message types and format,
		// see the method GamePlayer.handleGameMessage() in the game-client-api
		// document.
		switch (messageType) {
            case GameMessage.GAME_STATE_BOARD:		
            			
                ArrayList<Integer> stateArr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.GAME_STATE));
                this.getGameGUI().setGameState(stateArr);
                this.MCTree = null;
                System.out.println("Set game board.");
                break; 

            case GameMessage.GAME_ACTION_MOVE:
                getGameGUI().updateGameState(msgDetails);

                Move opponentsMove = new Move(msgDetails);
                System.out.println("Opponent's Move: " + opponentsMove.toString());

                if(this.MCTree != null) {
                    this.MCTree.moveRootAfterMove(opponentsMove);
                    makeAIMove();
                }
                
                break;

            case GameMessage.GAME_ACTION_START:

                if(this.userName().equals(msgDetails.get("player-black")))
                    this.player = BLACK;
                else if(this.userName().equals(msgDetails.get("player-white")))
                    this.player = WHITE;
                
                System.out.println("Game Start: Black Played by " + msgDetails.get("player-black"));
                System.out.println("Game Start: White Played by " + msgDetails.get("player-white"));

                System.out.println("Timer Started on Black");

                InitalizeBoard();

                if(this.player == BLACK){
                    makeOpeningMove();
                }
            
                break;
                
            default:
                assert (false);
                break;
            }
		return true;
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

        Move moveForServer = move.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);
    }
	
	public void makeAIMove() {
		
		Move bestMove = this.MCTree.MCTS();
    
        if(bestMove.isNull()){
            System.out.println("----------------------------------");
            System.out.println("Nah I'd win (we lost)");
            System.out.println("----------------------------------");
            return;
        }

        this.MCTree.moveRootAfterMove(bestMove);

        Move moveForServer = bestMove.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);
	}
	
	public void InitalizeBoard() {
		this.MCTree = new MonteCarlo(new Node(new Board(this.player == BLACK)), maxTime, 1.4);
	}

	@Override
	public String userName() {
		return userName;
	}

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return this.gamegui;
	}

	@Override
	public void connect() {
		gameClient = new GameClient(userName, passwd, this);
	}

}// end of class
