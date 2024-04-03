import java.util.ArrayList;
import java.util.Arrays;

import ubc.GameState.Board;
import ubc.GameState.Move;
import ubc.GameState.MoveGenerator;

public class BoardTest {
    public static void main(String[] args){
        byte[][] sampleBoard = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,2,0,0,0,0,0,2,0},
            {0,0,2,0,0,0,0,1,0,0},
            {0,0,0,0,2,0,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,1,0,0,0,0,0,0,0,0},
            {1,0,0,0,0,0,0,0,0,0}
        };

        Board board = new Board(sampleBoard);
        System.out.println(board.toString());
        
        int blackDistancePenalty = 0, whiteDistancePenalty = 0;

        int[][] blackQueens = board.getBlackQueens();
        for(int[] queen: blackQueens){
            for(int[] queen2: blackQueens){
                if((queen[0] != queen2[0]) || (queen[1] != queen2[1])){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if((queen[0] - queen2[0]) == (queen[1] - queen2[1])){
                        distance = distance/2;
                    }
                    if(distance == 1){
                        blackDistancePenalty += (int)(30*distance)/2;
                    } else if(distance == 2){
                        blackDistancePenalty += (int)(20*distance)/2;
                    } else if(distance == 3){
                        blackDistancePenalty += (int)(10*distance)/2;
                    }
                }
            }
        }

        int[][] whiteQueens = board.getWhiteQueens();
        for(int[] queen: whiteQueens){
            for(int[] queen2: whiteQueens){
                if(queen[0] != queen2[0] || queen[1] != queen2[1]){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if(distance-1 == 1){
                        whiteDistancePenalty += (int)(30*distance)/2;
                    } else if(distance-1 == 2){
                        whiteDistancePenalty += (int)(20*distance)/2;
                    } else if(distance-1 == 3){
                        whiteDistancePenalty += (int)(10*distance)/2;
                    }
                }
            }
        }

        System.out.println("Black distance penalty: " + blackDistancePenalty);
        System.out.println("White distance penalty: " + whiteDistancePenalty);
    }
}       
