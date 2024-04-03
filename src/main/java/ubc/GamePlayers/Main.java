package ubc.GamePlayers;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

public class Main {
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {	
 
    	// Jarvis_v1 player1 = new Jarvis_v1("Jarvis", "cosc322", 5, 28000, false);
        // Gambler player2 = new Gambler("Ultron", "cosc322", 5, false);
        // Jarvis_v2 player2 = new Jarvis_v2("Jarvis 2.0", "cosc322", 5, 5000, true);
        Jarvis_v3 player1 = new Jarvis_v3("Jarvis Deepening c0mplex", "cosc322", 5, 6, 15000, false);
        // Jarvis_v3 player1 = new Jarvis_v3("Jarvis Deepening c0mplex 2.0", "cosc322", 5, 5, 10000, true);
        // HumanPlayer player2 = new HumanPlayer();
    	
    	if(player1.getGameGUI() == null) {
    		player1.Go();
            // player2.Go();
    	}

    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player1.Go();
                    // player2.Go();
                }
            });
    	}
    }
}
