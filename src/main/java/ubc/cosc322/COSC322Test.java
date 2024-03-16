
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.BoardGameModel;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GameModel;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.Amazon.GameBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private BoardGameModel board = null;
    private GameMessage msg = null;
    // private Amazon amazon = null;
    private GameBoard boardgame = null;
    
    private GameModel gameModel = null;

    // private ArrayList<Integer> gameState = new ArrayList<Integer>(100);

    public Board gameBoard = new Board();
	
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

        String roomToJoin = rooms.get(0).getName();
        System.out.println("Joining Room: " + roomToJoin);
        gameClient.joinRoom(roomToJoin);

        userName = gameClient.getUserName();
        if(gamegui != null){
            gamegui.setRoomInformation(gameClient.getRoomList());
        }

    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		System.out.println("AI Player received game message - Type : "+ messageType + ", Details: " + msgDetails);        

        if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {

            gamegui.setGameState((ArrayList<Integer>) msgDetails.get("game-state"));
            // gameBoard.printBoard();

        } else if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {

            gamegui.updateGameState(msgDetails);
            System.out.println("Game State: " + msgDetails.get("player-black"));
            // System.out.println(msgDetails.get("queen-position-current"));

        } else if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            if(this.userName().equals(msgDetails.get("player-black"))){
                gameBoard.setUp(true);
            } else if(this.userName().equals(msgDetails.get("player-white"))){
                gameBoard.setUp(false);
            }

            System.out.println("Game Start: Black Played by " + msgDetails.get("player-black"));
            System.out.println("Game Start: White Played by " + msgDetails.get("player-white"));

            System.out.println("Timer Started on Black");
            // ArrayList<Integer> currentPos = new ArrayList<>(Arrays.asList(1,4));
            // ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(4,4));
            // ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(5,5));
            // gameClient.sendMoveMessage(currentPos, newPos, arrowPos);
            // gamegui.updateGameState(currentPos, newPos, arrowPos);
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

    public void makeMove() {

        //Make ArrayList of current position of 8 queens, {r1,r2,r3,r4,c1,c2,c3,c4}, pick random number from 1-4 for queen, then pick a random direction 1-3 for direction, then choose coordinates in that direction. Arrow goes to queen's old position. 

        System.out.println("Making Move");
        ArrayList<Integer> currentPos = new ArrayList<>(Arrays.asList(1,4));
        ArrayList<Integer> newPos = new ArrayList<>(Arrays.asList(4,4));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(5,5));
        gameClient.sendMoveMessage(currentPos, newPos, arrowPos);
    }
 
}//end of class
