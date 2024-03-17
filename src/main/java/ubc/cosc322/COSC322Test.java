
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;
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
public class COSC322Test extends GamePlayer{

    public final int EMPTY = 0, WHITE = 1, BLACK = 2, ARROW = 3, BOARD_SIZE = 10;

    private GameClient gameClient; 
    private BaseGameGUI gamegui;

    private String userName;
    private String passwd;
	
    private Board board;
	
    private int color;
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	COSC322Test player1 = new COSC322Test("Jarvis", args[1]);

        HumanPlayer player2 = new HumanPlayer();
    	
    	if(player1.getGameGUI() == null || player2.getGameGUI() == null) {
    		player1.Go();
            player2.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player1.Go();
                    player2.Go();
                }
            });
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
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

		List<Room>  rooms = gameClient.getRoomList();

        // if(rooms.isEmpty()){
        //     System.out.println("No rooms available right now!");
        // }

        String roomToJoin = rooms.get(rooms.size()-1).getName();
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

            Move opponentsMove = new Move(msgDetails);
            System.out.println("Opponent's Move: " + opponentsMove.toString() + "\nOpponent's Move: " + opponentsMove.toStringServer());

            this.board.makeMove(opponentsMove, true);
            makeRandomMove();

        } else if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            if(this.userName().equals(msgDetails.get("player-black"))){
                board = new Board(true);
                this.color = BLACK;
            } else if(this.userName().equals(msgDetails.get("player-white"))){
                board = new Board(false);
                this.color = WHITE;
            }

            System.out.println("Game Start: Black Played by " + msgDetails.get("player-black"));
            System.out.println("Game Start: White Played by " + msgDetails.get("player-white"));

            System.out.println("Timer Started on Black");

            if(this.color == BLACK){
                makeRandomMove();
                // makeSampleMove();
            }
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

    private void makeRandomMove() {

		ArrayList<Move> moves = MoveGenerator.getAllMoves(this.board, this.color);
		Move randomMove = moves.get((int) (Math.random() * moves.size()));
        System.out.println("Random Move: " + randomMove.toString());

		this.board.makeMove(randomMove);

        Move moveForServer = randomMove.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        getGameClient().sendMoveMessage(currentPos, newPos, arrowPos);
        getGameGUI().updateGameState(currentPos, newPos, arrowPos);
    }

    private void makeSampleMove() {
        Move move = new Move(3,0,3,3,4,4);
        this.board.makeMove(move);

        Move moveForServer = move.getMoveForServer();
        ArrayList<Integer> currentPos = moveForServer.getOldPos(), newPos = moveForServer.getNewPos(), arrowPos = moveForServer.getArrowPos();

        gameClient.sendMoveMessage(currentPos, newPos, arrowPos);
        gamegui.updateGameState(currentPos, newPos, arrowPos);
    }
 
}//end of class
