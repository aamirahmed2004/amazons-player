package ubc.GameState;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluator {

    private Board board;

    public Evaluator(Board board){
        this.board = board;
    }

    public final int QUEEN = 1, KING = 2;

    public int simpleEval(){

        int eval = MoveGenerator.getAllMoves(board, Board.BLACK).size() - MoveGenerator.getAllMoves(board, Board.WHITE).size();
        int perspective = (board.blackToMove()) ? 1 : -1;

        // If black to move, return positive value when num moves for black > num moves for white
        return eval * perspective;
    }

    public int newEval(int numberOfMoves){

        int friendlyKingMoves = 0, enemyKingMoves = 0, friendlyQueenMoves = 0, enemyQueenMoves = 0;
        int trappedQueenPenalty = 0, trappedQueenBonus = 0;

        int[][] friendlyQueens = (board.blackToMove()) ? board.getBlackQueens() : board.getWhiteQueens();
        int[][] enemyQueens = (board.blackToMove()) ? board.getWhiteQueens() : board.getBlackQueens();

        for(int[] queen: friendlyQueens){
            int kingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            int queenMoves = MoveGenerator.getChessQueenMovesFromSquare(queen, board).size();
            friendlyKingMoves += kingMoves;
            friendlyQueenMoves += queenMoves;
            if(kingMoves == 0) trappedQueenPenalty += 50;
            else if(kingMoves < 3 || queenMoves < 6) trappedQueenPenalty += 20;
        }
        
        for(int[] queen: enemyQueens){
            int kingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            int queenMoves = MoveGenerator.getChessQueenMovesFromSquare(queen, board).size();
            enemyKingMoves += kingMoves;
            enemyQueenMoves += queenMoves;
            if(kingMoves == 0) trappedQueenBonus += 50;
            else if(kingMoves < 3 || queenMoves < 6) trappedQueenBonus += 20;
        }

        int eval = (1-(numberOfMoves/50))*(friendlyKingMoves - enemyKingMoves) + (numberOfMoves/50)*(friendlyQueenMoves - enemyQueenMoves) 
                    - trappedQueenPenalty + trappedQueenBonus;

        return eval;
    }

    public int arrowEval(){

        int eval = 0;

        int[][] friendlyQueens = (board.blackToMove()) ? board.getBlackQueens() : board.getWhiteQueens();
        for(int[] queen: friendlyQueens){
            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            eval += numKingMoves;
            if (numKingMoves == 0) eval -= 10;
            else if (numKingMoves < 3) eval -= 5;
        }

        int[][] enemyQueens = (board.blackToMove()) ? board.getWhiteQueens() : board.getBlackQueens();
        for(int[] queen: enemyQueens){
            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            eval -= numKingMoves;
            if (numKingMoves == 0) eval += 10;
            else if (numKingMoves < 3) eval += 5;
        }

        return eval;
    }

    public int customEval(int numberOfMoves){

        int blackMoves = (int) MoveGenerator.getAllMoves(board, Board.BLACK).size()/10;
        int whiteMoves = (int) MoveGenerator.getAllMoves(board, Board.WHITE).size()/10;

        int f_Q1 = 0, f_Q2 = 0, f_Q3 = 0, f_Q4 = 0, w_Q1 = 0, w_Q2 = 0, w_Q3 = 0, w_Q4 = 0;

        int blackCornerPenalty = 0, whiteCornerPenalty = 0;
        int blackDistancePenalty = 0, whiteDistancePenalty = 0;
        int blackEdgePenalty = 0, whiteEdgePenalty = 0;
        int modifier = 0, whiteArrowPenalty = 0;

        int[][] blackQueens = board.getBlackQueens();
        for(int[] queen: blackQueens){

            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            if(numKingMoves == 0){
                modifier += 50;
            } else if(numKingMoves >= 1 && numKingMoves <= 3){
                modifier += 25;
            }

            blackMoves *= numKingMoves/4;

            if(queen[0] < 5 && queen[1] < 5) 
                f_Q1 = (f_Q1 == 0) ? f_Q1 + 10 : f_Q1 - 3;
            else if(queen[0] < 5 && queen[1] >= 5) 
                f_Q2 = (f_Q2 == 0) ? f_Q2 + 10 : f_Q2 - 3;
            else if(queen[0] >= 5 && queen[1] < 5)
                f_Q3 = (f_Q3 == 0) ? f_Q3 + 10 : f_Q3 - 3;
            else f_Q4 = (f_Q4 == 0) ? f_Q4 + 10 : f_Q4 - 3;

            if(queen[0] <= 1 && queen[1] <= 1){
                blackCornerPenalty += 50;
            } else if(queen[0] <= 1 && queen[1] >= 8){
                blackCornerPenalty += 50;
            } else if(queen[0] >= 8 && queen[1] <= 1){
                blackCornerPenalty += 50;
            } else if(queen[0] >= 8 && queen[1] >= 8){
                blackCornerPenalty += 50;
            }

            if(queen[0] == 0 || queen[0] == Board.BOARD_SIZE-1 || queen[1] == 0 || queen[1] == Board.BOARD_SIZE-1){
                blackEdgePenalty += 25;
            }

            for(int[] queen2: blackQueens){
                if((queen[0] != queen2[0]) || (queen[1] != queen2[1])){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if((queen[0] - queen2[0]) == (queen[1] - queen2[1])){
                        distance = distance/2;
                    }
                    if(distance == 1){
                        blackDistancePenalty += (int)(20*distance)/2;
                    } else if(distance == 2){
                        blackDistancePenalty += (int)(12*distance)/2;
                    } else if(distance == 3){
                        blackDistancePenalty += (int)(4*distance)/2;
                    }
                }
            }
        }

        int[][] whiteQueens = board.getWhiteQueens();
        for(int[] queen: whiteQueens){

            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            if(numKingMoves == 0){
                whiteArrowPenalty += 50;
            } else if(numKingMoves >= 1 && numKingMoves <= 3){
                whiteArrowPenalty += 25;
            }

            whiteMoves *= numKingMoves/4;

            if(queen[0] < 5 && queen[1] < 5) 
                w_Q1 = (w_Q1 == 0) ? w_Q1 + 10 : w_Q1 - 3;
            else if(queen[0] < 5 && queen[1] >= 5) 
                w_Q2 = (w_Q2 == 0) ? w_Q2 + 10 : w_Q2 - 3;
            else if(queen[0] >= 5 && queen[1] < 5)
                w_Q3 = (w_Q3 == 0) ? w_Q3 + 10 : w_Q3 - 3;
            else w_Q4 = (w_Q4 == 0) ? w_Q4 + 10 : w_Q4 - 3;

            if(queen[0] <= 1 && queen[1] <= 1){
                whiteCornerPenalty += 50;
            } else if(queen[0] <= 1 && queen[1] >= 8){
                whiteCornerPenalty += 50;
            } else if(queen[0] >= 8 && queen[1] <= 1){
                whiteCornerPenalty += 50;
            } else if(queen[0] >= 8 && queen[1] >= 8){
                whiteCornerPenalty += 50;
            }

            if(queen[0] == 0 || queen[0] == Board.BOARD_SIZE-1 || queen[1] == 0 || queen[1] == Board.BOARD_SIZE-1){
                whiteEdgePenalty += 25;
            }

            for(int[] queen2: whiteQueens){
                if((queen[0] != queen2[0]) || (queen[1] != queen2[1])){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if((queen[0] - queen2[0]) == (queen[1] - queen2[1])){
                        distance = distance/2;
                    }
                    if(distance == 1){
                        whiteDistancePenalty += (int)(20*distance)/2;
                    } else if(distance == 2){
                        whiteDistancePenalty += (int)(12*distance)/2;
                    } else if(distance == 3){
                        whiteDistancePenalty += (int)(4*distance)/2;
                    }
                }
            }
        }

        int blackScore = blackMoves - blackCornerPenalty - blackDistancePenalty - blackEdgePenalty - modifier;
        int whiteScore = whiteMoves - whiteCornerPenalty - whiteDistancePenalty - whiteEdgePenalty - whiteArrowPenalty;

        if(numberOfMoves < 50){
            blackScore += (1-(numberOfMoves/50))*(f_Q1 + f_Q2 + f_Q3 + f_Q4);
            whiteScore += (1-(numberOfMoves/50))*(w_Q1 + w_Q2 + w_Q3 + w_Q4);
        }

        int eval = blackScore - whiteScore;
        int perspective = (board.blackToMove()) ? 1 : -1;

        return eval * perspective;
    }

    public int modifiedNotSoSimpleEval(int numberOfMoves){
        int eval = notSoSimpleEval(numberOfMoves);
        double modifier = 5;
        int f_Q1 = 0, f_Q2 = 0, f_Q3 = 0, f_Q4 = 0, w_Q1 = 0, w_Q2 = 0, w_Q3 = 0, w_Q4 = 0;

        if(numberOfMoves > 40) return eval * (int)modifier;

        int[][] friendlyQueens = (board.blackToMove()) ? board.getBlackQueens() : board.getWhiteQueens();

        for(int[] queen: friendlyQueens){

            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            if(numKingMoves == 0){
                modifier -= 1;
            } else if(numKingMoves >= 1 && numKingMoves <= 3){
                modifier -= 0.4;
            }

            if(queen[0] <= 1 && queen[1] <= 1){
                modifier -= 0.50;
            } else if(queen[0] <= 1 && queen[1] >= 8){
                modifier -= 0.50;
            } else if(queen[0] >= 8 && queen[1] <= 1){
                modifier -= 0.50;
            } else if(queen[0] >= 8 && queen[1] >= 8){
                modifier -= 0.50;
            }

            if(queen[0] == 0 || queen[0] == Board.BOARD_SIZE-1 || queen[1] == 0 || queen[1] == Board.BOARD_SIZE-1){
                modifier -= 0.25;
            }

            for(int[] queen2: friendlyQueens){
                if((queen[0] != queen2[0]) || (queen[1] != queen2[1])){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if((queen[0] - queen2[0]) == (queen[1] - queen2[1])){
                        distance = distance/2;
                    }
                    if(distance == 1){
                        modifier -= 0.15;
                    } else if(distance == 2){
                        modifier -= 0.10;
                    } 
                }
            }
        }

        int[][] enemyQueens = (board.blackToMove()) ? board.getWhiteQueens() : board.getBlackQueens();

        for(int[] queen: enemyQueens){

            int numKingMoves = MoveGenerator.getChessKingMovesFromSquare(queen, board).size();
            if(numKingMoves == 0){
                modifier += 1;
            } else if(numKingMoves >= 1 && numKingMoves <= 3){
                modifier += 0.4;
            }

            if(queen[0] <= 1 && queen[1] <= 1){
                modifier += 0.50;
            } else if(queen[0] <= 1 && queen[1] >= 8){
                modifier += 0.50;
            } else if(queen[0] >= 8 && queen[1] <= 1){
                modifier += 0.50;
            } else if(queen[0] >= 8 && queen[1] >= 8){
                modifier += 0.50;
            }

            if(queen[0] == 0 || queen[0] == Board.BOARD_SIZE-1 || queen[1] == 0 || queen[1] == Board.BOARD_SIZE-1){
                modifier += 0.25;
            }

            for(int[] queen2: enemyQueens){
                if((queen[0] != queen2[0]) || (queen[1] != queen2[1])){
                    int distance = Math.abs(queen[0] - queen2[0]) + Math.abs(queen[1] - queen2[1]);
                    if((queen[0] - queen2[0]) == (queen[1] - queen2[1])){
                        distance = distance/2;
                    }
                    if(distance == 1){
                        modifier += 0.15;
                    } else if(distance == 2){
                        modifier += 0.10;
                    } 
                }
            }
        }

        if(modifier == 0.0) return eval;

        return eval * (int)modifier;
    }

    public int notSoSimpleEval(int numberOfMoves){

        int perspective = (board.blackToMove()) ? 1 : -1;

        // Start with evaluations that need queen distances
        double t1 = 0.0, c1 = 0.0, w = 0.0;
        int[][] blackMinDistances_Queen = getPlayerMinDistances(Board.BLACK, QUEEN,numberOfMoves);
        int[][] whiteMinDistances_Queen = getPlayerMinDistances(Board.WHITE, QUEEN,numberOfMoves);

        // Compute the following sums for every empty square
        for(int i = 0; i < Board.BOARD_SIZE; i++){
            for(int j = 0; j < Board.BOARD_SIZE; j++){
                
                if(!board.isEmpty(i,j)) continue;

                c1 += Math.pow(2, -blackMinDistances_Queen[i][j]) - Math.pow(2, -whiteMinDistances_Queen[i][j]);
                
                // Compute this sum for every square that is reachable 
				if((whiteMinDistances_Queen[i][j] != Integer.MAX_VALUE) && (blackMinDistances_Queen[i][j] != Integer.MAX_VALUE)) 
					w += Math.pow(2, -Math.abs(whiteMinDistances_Queen[i][j] - blackMinDistances_Queen[i][j]));
                
                // Computing t1 based on the paper
                // if black controls the square: t1 += 1, if white controls the square: t1 -= 1
				if(blackMinDistances_Queen[i][j] < whiteMinDistances_Queen[i][j]) 
					t1 += 1.0 * perspective;

                else if(blackMinDistances_Queen[i][j] > whiteMinDistances_Queen[i][j]) 
                    t1 -= 1.0 * perspective;
				
				// else if distances are equal and square is reachable: t1 is incremented by K depending on the player whose turn it is. We use |k| = 0.2 
				else if(blackMinDistances_Queen[i][j] != Integer.MAX_VALUE) 
					t1 += 0.2 * perspective;
			
            }
        }

        c1 = c1 * perspective;

        // Now calculate evaluations that need king distances
        double t2 = 0.0, c2 = 0.0;
        int[][] blackMinDistances_King = getPlayerMinDistances(Board.BLACK, KING);
        int[][] whiteMinDistances_King = getPlayerMinDistances(Board.WHITE, KING);

        // Compute the following sums for every empty square
        for(int i = 0; i < Board.BOARD_SIZE; i++){
            for(int j = 0; j < Board.BOARD_SIZE; j++){
                
                if(!board.isEmpty(i,j)) continue;

                c2 += Math.min(1, Math.max(-1, (blackMinDistances_King[i][j] - whiteMinDistances_King[i][j])/6.0));
                
                // Computing t2 based on the paper
                // if black controls the square: t2 += 1, if white controls the square: t2 -= 1
				if(blackMinDistances_King[i][j] < whiteMinDistances_King[i][j]) 
					t2 += 1.0 * perspective;
				
                else if(blackMinDistances_King[i][j] > whiteMinDistances_King[i][j]) 
                    t2 -= 1.0 * perspective;    
                    
				// else if distances are equal and square is reachable: t2 is incremented by K depending on the player whose turn it is. We use |k| = 0.2 
				else if(blackMinDistances_King[i][j] != Integer.MAX_VALUE) 
					t2 += 0.2 * perspective;	
            }
        }

        c2 = c2 * perspective;

        // TODO: implement functions f_1(w) through f_4(w) such that sigma f_i(w) = 1, f_1(0) = 1, and f_4(0) = 0. 
        double t = f1(w)*t1 + (1-f1(w))*t2;
        double m = mobilityEval(w);
        
        int eval = (int)(t+m);
        return eval;
    }

    public double mobilityEval(double w){
        
        byte[][] mobilities = getKingMoveMobilities();
        double blackEval = getPlayerMobilityEval(mobilities, Board.BLACK, w);
        double whiteEval = getPlayerMobilityEval(mobilities, Board.WHITE, w);        

        double eval = blackEval - whiteEval;
        int perspective = (board.blackToMove()) ? 1 : -1;
        return eval * perspective; 
    }

    // Returns a 2D array where the value of each entry is the number of Chess King moves possible from the corresponding square on the given board.
    public byte[][] getKingMoveMobilities(){

        byte[][] mobilities = new byte[Board.BOARD_SIZE][Board.BOARD_SIZE];

        for(int i = 0; i < Board.BOARD_SIZE; i++){
            for(int j = 0; j < Board.BOARD_SIZE; j++){
                if(board.isEmpty(i, j)){
                    int[] square = new int[]{i,j};
                    mobilities[i][j] = (byte) MoveGenerator.getChessKingMovesFromSquare(square, board).size();
                }    
            }
        }
        return mobilities;
    }

    // Given a player (white/black) and a type (king/queen), returns a 2D array containing the minimum distances from each square 
    public int[][] getPlayerMinDistances(int player, int type, int numberOfMoves){

        int[][] playerDistances = new int[Board.BOARD_SIZE][Board.BOARD_SIZE];
        int[][] playerQueens = (player == Board.BLACK) ? board.getBlackQueens() : board.getWhiteQueens();

        // Initialize queen distances array to 0 for all non-empty squares, max value for all empty squares
        for(int i = 0; i < Board.BOARD_SIZE; i++){
            for(int j = 0; j < Board.BOARD_SIZE; j++){
                if(!board.isEmpty(i, j)) 
                    playerDistances[i][j] = 0;
                else 
                    playerDistances[i][j] = Integer.MAX_VALUE;
            }
        }

        /*
         * Algorithm from Hensgens' A Knowledge-based Approach of the Game of Amazons: 
         * 
         * for every square in squaresToCheck
         *      for every king/queen move from that square
         *          update distances array, setting distance to Y (where Y is the current distance)
         * 
         * for every square that is unupdated (i.e. distance = max value)
         *      for every king/queen move from that square
         *          if move gets to a square with distance Y, update distances array, setting distance to Y+1
         * 
         * if looking for queen distances early in the game, stop after 1 iteration: 
         *      update all squares that still have distance = max value to Y+2 (should be 3).
         * 
         * else
         *      add all squares that still have distance = max value to squaresToCheck
         *      repeat until distances array remains unchanged after iteration           
         */     
        
        ArrayList<int[]> squaresToCheck = new ArrayList<>(Arrays.asList(playerQueens));
        boolean changesMade = false;
        int currentDistance = 0;
        boolean restrictedSearch = (type == QUEEN && numberOfMoves < 30);

        do{
            changesMade = false; currentDistance++;

            for(int[] square: squaresToCheck){

                ArrayList<ArrayList<Integer>> possibleMoves = (type == QUEEN) ? 
                        MoveGenerator.getChessQueenMovesFromSquare(square, board) : MoveGenerator.getChessKingMovesFromSquare(square,board);

                for(ArrayList<Integer> move: possibleMoves){
                    int row = move.get(0), col = move.get(1);
                    if(playerDistances[row][col] > currentDistance){
                        playerDistances[row][col] = currentDistance;
                        changesMade = true;
                    }
                }
            }

            currentDistance++;

            for(int i = 0; i < Board.BOARD_SIZE; i++){
                for(int j = 0; j < Board.BOARD_SIZE; j++){

                    if (playerDistances[i][j] == Integer.MAX_VALUE){

                        int[] square = new int[]{i,j};
                        ArrayList<ArrayList<Integer>> possibleSecondMoves = (type == QUEEN) ? 
                            MoveGenerator.getChessQueenMovesFromSquare(square, board) : MoveGenerator.getChessKingMovesFromSquare(square,board);

                        for(ArrayList<Integer> move: possibleSecondMoves){
                            int row = move.get(0), col = move.get(1);
                            if(playerDistances[row][col] == currentDistance-1){
                                playerDistances[row][col] = currentDistance;
                                changesMade = true;
                            }
                        }
                    }
                }
            }

            squaresToCheck.clear();

            for(int i = 0; i < Board.BOARD_SIZE; i++){
                for(int j = 0; j < Board.BOARD_SIZE; j++){

                    if (playerDistances[i][j] == Integer.MAX_VALUE){
                        
                        if(restrictedSearch){
                            playerDistances[i][j] = currentDistance + 1;
                        } else {
                            int[] square = new int[]{i,j};
                            squaresToCheck.add(square);
                        }
                    }
                }
            }
            
        } while(changesMade && !restrictedSearch);

        return playerDistances;
    }

    public int[][] getPlayerMinDistances(int player, int type){
        return getPlayerMinDistances(player, type, 0);
    }

    public double getPlayerMobilityEval(byte[][] mobilities, int player, double w){

        /*
        * Get sum of alpha values for the player's queens
        * Algorithm: 
        * 
        * for each queen
        *
        *   for every move along a particular direction:
        *       if next square is occupied, stop
        *       else, add mobility t1 of that square
        *
        *   repeat for all 8 directions
        */

        double playerEval = 0;
        int[][] playerQueens = (player == Board.BLACK) ? board.getBlackQueens() : board.getWhiteQueens();

        for(int index = 0; index < playerQueens.length; index++){

            int row = playerQueens[index][0], col = playerQueens[index][1]; 
            double queenMobility = 0;

            // Right
            for (int i = 1; row + i < Board.BOARD_SIZE; i++) {    
                if (!board.isEmpty(row + i, col)) break;
                byte numberOfReachableSquares = mobilities[row+i][col];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Left
            for (int i = 1; row - i >= 0; i++) {
                if (!board.isEmpty(row - i, col)) break;
                byte numberOfReachableSquares = mobilities[row-i][col];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up
            for (int i = 1; col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row, col + i)) break;
                byte numberOfReachableSquares = mobilities[row][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down
            for (int i = 1; col - i >= 0; i++) {
                if (!board.isEmpty(row, col - i)) break;
                byte numberOfReachableSquares = mobilities[row][col-i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up right
            for (int i = 1; row + i < Board.BOARD_SIZE && col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row + i, col + i)) break;
                byte numberOfReachableSquares = mobilities[row+i][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down left
            for (int i = 1; row - i >= 0 && col - i >= 0; i++) {
                if (!board.isEmpty(row - i, col - i)) break;
                byte numberOfReachableSquares = mobilities[row-i][col-i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Down right
            for (int i = 1; row + i < Board.BOARD_SIZE && col - i >= 0; i++) {
                if (!board.isEmpty(row + i, col - i)) break;
                byte numberOfReachableSquares = mobilities[row+i][col-1];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }

            // Up left
            for (int i = 1; row - i >= 0 && col + i < Board.BOARD_SIZE; i++) {
                if (!board.isEmpty(row - i, col + i)) break;
                byte numberOfReachableSquares = mobilities[row-i][col+i];
                int kingDistanceFromStartingSquare = i-1;
                queenMobility += Math.pow(2,-kingDistanceFromStartingSquare) * numberOfReachableSquares;
            }
            playerEval += f(w,queenMobility);    
        }

        return playerEval;
    }

    /*
     * Following Jens Lieberum's evaluation function paper, we chose a non-linear function f(w,alpha) such that: 
     *  f(0,alpha) == 0, partial df/d(w) >= 0 and partial df/d(alpha) <= 0
     * 
     * We also followed the recommendation that 2f(w,5) < f(w,0).
     * 
     * We decided on the following function that satisfies these criteria. 
     * 
     *  f(w, alpha) = w * 1.15^(-alpha) / 50
     */
    private double f(double w, double alpha){
        return w / (50 * Math.pow(1.15, alpha));
    }

    private double f1(double w){
        return (100-w)/100.0;
    }

    private double f2(double w){
        return w/400.0;
    }

    private double f3(double w){
        return w/400.0;
    }

    private double f4(double w){
        return w/200.0;
    }
}
