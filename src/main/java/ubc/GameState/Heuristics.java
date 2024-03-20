package ubc.GameState;

import java.util.ArrayList;

public class Heuristics {

    public static byte[][][] getQueenDistancesForEveryTile(Board board, int[][] friendlyQueens, int[][] enemyQueens){

        byte[][][] queenDistances = new byte[10][10][2];

        for(int i = 0; i < queenDistances.length; i++){
            for(int j = 0; j < queenDistances[i].length; j++){

                if(!board.isEmpty(j, i)){
                    queenDistances[j][i][0] = queenDistances[j][i][1] = Byte.MIN_VALUE;
                }

                else{
                    queenDistances[j][i][0] = queenDistances[j][i][1] = 0;
                }
            }
        }
    
        for(int[] queen: friendlyQueens){
            ArrayList<ArrayList<Integer>> squares = MoveGenerator.getChessQueenMovesFromSquare(queen, board);
            for(ArrayList<Integer> square: squares){
                int newX = (int) square.get(0), newY = (int) square.get(1);
                queenDistances[newX][newY][0] += 1;
            }
        }


        return queenDistances;
    } 

    public static int simpleEval(Board board){
        return (MoveGenerator.getAllMoves(board, Board.BLACK).size() - MoveGenerator.getAllMoves(board, Board.WHITE).size());
    }
}
