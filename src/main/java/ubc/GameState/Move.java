package ubc.GameState;

import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Move {
    
    // Use arraylists to store the previous and current positions of the queen, as well as the arrow position
    private final ArrayList<Integer> oldPos;
    private final ArrayList<Integer> newPos;
    private final ArrayList<Integer> arrowPos;

    /**
     * Converts the map receive from the server to a move. 
     * Coordinates in the input are of the form (y,x) and are 1 indexed (i.e start from 1 instead of 0) and are converted to 0 indexed internally.
     * @param msgDetails The map sent by the server when messageType is cosc322.game-action.move
     */
    @SuppressWarnings("unchecked")
    public Move(Map<String, Object> msgDetails) {

        // Cloning the values to prevent the original map from being modified, since we need to convert to 0 indexing.
        oldPos = (ArrayList<Integer>) ((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR)).clone();
        newPos = (ArrayList<Integer>) ((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT)).clone();
        arrowPos = (ArrayList<Integer>) ((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS)).clone();

        int oldX = oldPos.get(1);
        int oldY = oldPos.get(0);
        int newX = newPos.get(1);
        int newY = newPos.get(0);
        int arrowX = arrowPos.get(1);
        int arrowY = arrowPos.get(0);

        oldPos.set(0, oldX - 1);
        oldPos.set(1, oldY - 1);
        newPos.set(0, newX - 1);
        newPos.set(1, newY - 1);
        arrowPos.set(0, arrowX - 1);
        arrowPos.set(1, arrowY - 1);
    }

    // Internal constructor
    Move(int oldX, int oldY, int newX, int newY, int arrowX, int arrowY) {
        oldPos = new ArrayList<>(Arrays.asList(oldX, oldY));
        newPos = new ArrayList<>(Arrays.asList(newX, newY));
        arrowPos = new ArrayList<>(Arrays.asList(arrowX, arrowY));
    }

    public ArrayList<Integer> getOldPos() {
        return oldPos;
    }

    public ArrayList<Integer> getNewPos() {
        return newPos;
    }

    public ArrayList<Integer> getArrowPos() {
        return arrowPos;
    }

    // Returns a string representation of the move in conventional notation: [oldX, oldY]-[newX, newY]/[arrowX, arrowY], except x-values are represented as letters
    @Override
    public String toString() {

        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};

        // We don't need to add 1 to convert x-values to 1-indexed since we just index letters array
        int oldX = oldPos.get(0), newX = newPos.get(0), arrowX = arrowPos.get(0);   
        // We add 1 to convert all y-values to 1-indexed
        int oldY = oldPos.get(1) + 1, newY = newPos.get(1) + 1, arrowY = arrowPos.get(1) + 1;   

        return letters[oldX] + oldY + "-" + letters[newX] + (newY) + "/" + letters[arrowX] + (arrowY);
    }

    // Returns a move in the format that the server expects
    public ArrayList<ArrayList<Integer>> sendMoveToServer() {

        ArrayList<ArrayList<Integer>> move = new ArrayList<>();

        // NOTE: The server expects the coordinates to be 1-indexed (y,x) values
        move.add(new ArrayList<>(Arrays.asList(oldPos.get(1) + 1, oldPos.get(0) + 1)));
        move.add(new ArrayList<>(Arrays.asList(newPos.get(1) + 1, newPos.get(0) + 1)));
        move.add(new ArrayList<>(Arrays.asList(arrowPos.get(1) + 1, arrowPos.get(0) + 1)));

        return move;
    }
}