package com.example.stuart.draughts;

import java.util.HashMap;

/**
 * Created by Stuart on 30/08/2016.
 * This is the Player class, of which two are created by the game object. They can be told to make a move, and if AI can then use minimax to choose a move itself.
 */

class Ai {

    private Boolean optionalCapture;
    boolean factor; //specifies whether extra code in heuristicV3 should be run

    Ai(Boolean optionalCapture){
        this.optionalCapture = optionalCapture;
        factor = true;
    }


    private class Data{
        Data(double value, int depth, int specificity){
            this.value = value;
            this.depth = depth;
            this.specificity = specificity; //0: ==;    1: <=;    2: >=
        }
        double value;
        int depth;
        int specificity;

    } //class holds data for the board- the value of the board, how deep the search and the inequality
    private class Key{
        Key(Board board, Boolean player1Turn){
            this.board = board;
            this.player1Turn = player1Turn;
        }
        Board board;
        Boolean player1Turn;
        @Override
        public boolean equals(Object key){
            if (!(key instanceof Key)){
                return false;
            }
            Key k = (Key) key;
            return (this.board.equals(k.board)) && (this.player1Turn == k.player1Turn);
        }
        @Override
        public int hashCode(){
            long blackPieces;
            if (player1Turn){
                blackPieces = ~board.getBlackPieces();
            } else {
                blackPieces = board.getBlackPieces();
            }
            return (int) ( (blackPieces ^ 0b1101010110101000101001010101011101010010101010L)
                    + (board.getWhitePieces() ^ 0b0101010110111101000010101010001000101010110101L)
                    + (board.getKings() ^ 0b1110000100010010101011010100101111110010010100L) );
        }
    } //class uniquely identifies a record- holds the board, and whose turn it is
    private HashMap<Key, Data> database = new HashMap<>();

    //Version 1 of the minimax algorithm, this version will likely be replaced for greater efficiency and/or accuracy
    //Testing indicates this algorithm reach 8 plies in around 5 seconds of desktop processing time
    //Special features: none (no ab pruning, no quiscience search, no hash table, nothing)
    double minimaxV1(Board board, boolean isBlack, int depth){

        if (depth == 0){ //if maximum depth is achieved, do not search further; perform the heuristic function
            return heuristicV1(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore; //the value of the best possible move
        double currentScore; //the value of the current move

        if (isBlack){ //if player is black, high scores are good, so default score is as low as possible
            bestScore = Double.NEGATIVE_INFINITY;
        } else { //if player white, low scores are good, so default score is as high as possible
            bestScore = Double.POSITIVE_INFINITY;
        }

        for (Board currentMove : availableMoves) { //for each move in the list of possible moves
            currentScore = minimaxV1(currentMove, !isBlack, depth - 1); //score of current move is found with minimax

            //isBlack XOR the current score is lower than the best score; IE true if black and current score is greater than the best, or if white and the current score is lower
            if (isBlack != (currentScore < bestScore)){ //is this move the best so far for the current player?
                bestScore = currentScore;
            }
        }

        return bestScore; //return best score out available moves

    } //DONE //TESTED

    //Version 2 of the minimax algorithm, including alpha-beta pruning
    //This version can reach 9 plies slightly faster than V1 reaches 7 plies, which makes sense (7 * 4/3 = 9.3333)
    double minimaxV2(Board board, boolean isBlack, int depth, double alpha, double beta){
        if (depth == 0){
            return heuristicV3(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore;
        double currentScore; //the value of the current move

        if (isBlack) {
            bestScore = -Double.MAX_VALUE;
            for (Board move : availableMoves) {
                currentScore = minimaxV2(move, !isBlack, depth - 1, alpha, beta);

                if (currentScore > bestScore){
                    bestScore = currentScore;

                    if (bestScore > alpha) {
                        alpha = bestScore;

                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
            //reward faster wins by decreasing a winning value the more moves it takes
            if (bestScore > Double.MAX_VALUE-20){
                return bestScore - 1;
            }
            return bestScore;

        } else {
            bestScore = Double.MAX_VALUE;
            for (Board move : availableMoves) {
                currentScore = minimaxV2(move, !isBlack, depth - 1, alpha, beta);

                if (currentScore < bestScore) {
                    bestScore = currentScore;

                    if (bestScore < beta) {
                        beta = bestScore;

                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }

            //reward faster wins by decreasing a winning value the more moves it takes
            if (bestScore < -Double.MAX_VALUE+20){
                return bestScore + 1;
            }
            return bestScore;
        }

    }

    //Version 3 of the minimax algorithm, including alpha-beta pruning and a simple quiscience algorithm
    //If at 9 plies it is in the middle of a capture, this version will keep searching a move until the capture sequence is complete
    double minimaxV3(Board board, boolean isBlack, int depth, double alpha, double beta){
        if (depth == 0){
            return heuristicV1(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore;
        double currentScore; //the value of the current move

        if (isBlack) {
            bestScore = -Double.MAX_VALUE;
            for (Board move : availableMoves) {

                if (move.whiteCount(Board.maskValid) < board.whiteCount(Board.maskValid) && depth < 2){
                    currentScore = minimaxV3(move, !isBlack, 1, alpha, beta);
                } else {
                    currentScore = minimaxV3(move, !isBlack, depth-1, alpha, beta);
                }


                if (currentScore > bestScore){
                    bestScore = currentScore;

                    if (bestScore > alpha) {
                        alpha = bestScore;

                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
            return bestScore;

        } else {
            bestScore = Double.MAX_VALUE;
            for (Board move : availableMoves) {

                if (move.blackCount(Board.maskValid) < board.blackCount(Board.maskValid) && depth < 2){
                    currentScore = minimaxV3(move, !isBlack, 1, alpha, beta);
                } else {
                    currentScore = minimaxV3(move, !isBlack, depth-1, alpha, beta);
                }

                if (currentScore < bestScore) {
                    bestScore = currentScore;

                    if (bestScore < beta) {
                        beta = bestScore;

                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
            }
            return bestScore;
        }

    }

    //Version 4 of the minimax algorithm adds use of a transposition table
    //Not really considered a success, slows the program down for the most part
    double minimaxV4(Board board, boolean isBlack, int depth, double alpha, double beta){
        if (depth == 0){
            return heuristicV1(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore;
        double currentScore; //the value of the current move
        int specificity = 0;

        if (isBlack) {
            bestScore = -Double.MAX_VALUE;
            for (Board move : availableMoves) {

                Key moveKey = new Key(move, true);
                Data moveData = database.get(moveKey);
                if ((moveData != null) && (moveData.depth >= depth) && ((moveData.specificity == 0) || (moveData.specificity == 2))){
                    currentScore = moveData.value;
                } else {
                    if ((move.whiteCount(Board.maskValid) < board.whiteCount(Board.maskValid)) && (depth < 2)) {
                        currentScore = minimaxV4(move, !isBlack, 1, alpha, beta);
                    } else {
                        currentScore = minimaxV4(move, !isBlack, depth - 1, alpha, beta);
                    }
                }


                if (currentScore > bestScore){
                    bestScore = currentScore;
                    if (bestScore > alpha) {
                        alpha = bestScore;
                        if (alpha >= beta) {
                            specificity = 2;
                            break;
                        }
                    }
                }
            }

            Key currentKey = new Key(board, true);
            Data currentData = database.get(currentKey);
            if (currentData == null || currentData.depth <= depth){
                Data newData = new Data(bestScore, depth, specificity);
                database.put(currentKey, newData);
            }

            return bestScore;

        } else {
            bestScore = Double.MAX_VALUE;
            for (Board move : availableMoves) {

                Key moveKey = new Key(move, false);
                Data moveData = database.get(moveKey);
                if ((moveData != null) && (moveData.depth >= depth) && ((moveData.specificity == 0) || (moveData.specificity == 1))){
                    currentScore = moveData.value;
                } else {
                    if (move.blackCount(Board.maskValid) < board.blackCount(Board.maskValid) && depth < 2){
                        currentScore = minimaxV4(move, !isBlack, 1, alpha, beta);
                    } else {
                        currentScore = minimaxV4(move, !isBlack, depth-1, alpha, beta);
                    }
                }


                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    if (bestScore < beta) {
                        beta = bestScore;
                        if (alpha >= beta) {
                            specificity = 1;
                            break;
                        }
                    }
                }
            }

            Key currentKey = new Key(board, false);
            Data currentData = database.get(currentKey);
            if (currentData == null || currentData.depth <= depth){
                Data newData = new Data(bestScore, depth, specificity);
                database.put(currentKey, newData);
            }

            return bestScore;
        }

    }

    //Version 5 of the minimax algorithm uses repeated searching at greater and greater depth to identify the move most likely to cause a cutoff
    double minimaxV5(Board board, boolean isBlack, int depth, double alpha, double beta){
        if (depth == 0){
            return heuristicV2(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore;
        double currentScore; //the value of the current move

        if (isBlack) {
            bestScore = -Double.MAX_VALUE;
            for (int d=1; d<=depth; d++) { //repeat search starting with 1 ply, then continue up to search of full depth
                bestScore = -Double.MAX_VALUE;
                double tempAlpha = alpha; //reset values between each iteration
                for (int i = 0; i < availableMoves.length; i++) { //iterate through available moves
                    Board move = availableMoves[i];

                    currentScore = minimaxV5(move, !isBlack, d-1, tempAlpha, beta); //minimax for result

                    if (currentScore > bestScore) {
                        bestScore = currentScore;

                        if (bestScore > tempAlpha) {
                            tempAlpha = bestScore;

                            if (tempAlpha >= beta) { //if cutoff
                                System.arraycopy(availableMoves, 0, availableMoves, 1, i);
                                availableMoves[0] = move; //insert cutoff-causing move at top of list
                                break;
                            }
                        }
                    }
                }
                d = Math.min(d+1, depth-1); //if no cutoff, increase d by 2 instead of one- unless d would be > depth, in which case set to depth
            }
            return bestScore;

        } else {
            bestScore = Double.MAX_VALUE;
            for (int d=1; d<=depth; d++) {
                bestScore = -Double.MAX_VALUE;
                double tempBeta = beta;
                for (int i = 0; i < availableMoves.length; i++) {
                    Board move = availableMoves[i];

                    currentScore = minimaxV5(move, !isBlack, d-1, alpha, tempBeta);

                    if (currentScore < bestScore) {
                        bestScore = currentScore;

                        if (bestScore < tempBeta) {
                            tempBeta = bestScore;

                            if (alpha >= tempBeta) {
                                System.arraycopy(availableMoves, 0, availableMoves, 1, i);
                                availableMoves[0] = move;
                                break;
                            }
                        }
                    }
                }
                d = Math.min(d+1, depth-1);
            }
            return bestScore;
        }

    }

    //the relative values of board positions and other figures
    private static final float baseValue = 100; //base value of every piece
    private static final float kingValue = 100; //kings are worth x more than base value
    private static final float backValue = 50; //pieces at the back are worth x more than base value
    private static final float centerValue = 20; //pieces in the center are worth x more than base value
    private static final float ratioConstant = 0; //changes the weight of a difference in piece count
    private static final float freePiece = 70; //pieces past all opposition which are guaranteed to become kings
    private static final float counterAdvance = 10; //the extra value a piece has for making it further across the board
    private static final float ultimateConstant = (float) 0.0000000001; //the weight given to the average progress of each player's counters

    //Version 1 of the heuristic algorithm, this version will likely be replaced for greater efficiency and/or accuracy
    //Ideas: incentives to keep back row intact, control central 8 squares, and get kings; value a piece difference greater with fewer pieces on board; blocking option?
    //MAYBE use a genetic algorithm to optimise these values?
    private static double heuristicV1(Board board){

        double score = 0;

        score += Long.bitCount(board.getBlackPieces()) * baseValue;
        score -= Long.bitCount(board.getWhitePieces()) * baseValue;

        score += Long.bitCount(board.blackKings()) * kingValue;
        score -= Long.bitCount(board.whiteKings()) * kingValue;

        score += board.blackCount(Board.maskBlackBack) * backValue;
        score -= board.whiteCount(Board.maskWhiteBack) * backValue;

        score += board.blackCount(Board.maskCenter) * centerValue;
        score -= board.whiteCount(Board.maskCenter) * centerValue;

        if (board.blackCount(Board.maskValid) >= board.whiteCount(Board.maskValid)) {
            score *= (Long.bitCount(board.getBlackPieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getWhitePieces()) + ratioConstant);
        } else {
            score *= (Long.bitCount(board.getWhitePieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getBlackPieces()) + ratioConstant);
        }

        return score;

    } //DONE //TESTED

    static double heuristicV2(Board board){

        double score = 0;

        score += Long.bitCount(board.getBlackPieces()) * baseValue;
        score -= Long.bitCount(board.getWhitePieces()) * baseValue;

        score += Long.bitCount(board.blackKings()) * kingValue;
        score -= Long.bitCount(board.whiteKings()) * kingValue;

        score += board.blackCount(Board.maskBlackBack) * backValue;
        score -= board.whiteCount(Board.maskWhiteBack) * backValue;

        score += board.blackCount(Board.maskCenter) * centerValue;
        score -= board.whiteCount(Board.maskCenter) * centerValue;

        if (board.blackCount(Board.maskValid) >= board.whiteCount(Board.maskValid)) {
            score *= (Long.bitCount(board.getBlackPieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getWhitePieces()) + ratioConstant);
        } else {
            score *= (Long.bitCount(board.getWhitePieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getBlackPieces()) + ratioConstant);
        }

        for (int position = 40; position > 4; position--) { //count back board positions
            if (board.isPlayer2(position)){ //keep going until a player2 piece is reached
                break;
            } else if (board.isPlayer1(position)){ //if player1, piece has gotten past all opposition
                score += freePiece;
            }
        }
        for (int position = 5; position < 41; position++) { //count up board positions
            if (board.isPlayer1(position)){ //keep going until a player1 piece is reached
                break;
            } else if (board.isPlayer2(position)){ //if player2, piece has gotten past all opposition
                score -= freePiece;
            }
        }

        return score;

    } //DONE //TESTED

    double heuristicV3(Board board){

        double score = 0;

        score += Long.bitCount(board.getBlackPieces()) * baseValue;
        score -= Long.bitCount(board.getWhitePieces()) * baseValue;

        score += Long.bitCount(board.blackKings()) * kingValue;
        score -= Long.bitCount(board.whiteKings()) * kingValue;

        score += board.blackCount(Board.maskBlackBack) * backValue;
        score -= board.whiteCount(Board.maskWhiteBack) * backValue;

        score += board.blackCount(Board.maskCenter) * centerValue;
        score -= board.whiteCount(Board.maskCenter) * centerValue;

        if (board.blackCount(Board.maskValid) >= board.whiteCount(Board.maskValid)) {
            score *= (Long.bitCount(board.getBlackPieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getWhitePieces()) + ratioConstant);
        } else {
            score *= (Long.bitCount(board.getWhitePieces()) + ratioConstant); //multiply the score by the ratio of white+constant : black+constant
            score /= (Long.bitCount(board.getBlackPieces()) + ratioConstant);
        }

        if (factor) {
            long flippedBlacks = Long.reverse(board.getBlackPieces()) >> 18;
            score += (flippedBlacks - board.getWhitePieces()) * ultimateConstant;
        }

        return score;

    } //DONE //TESTED

}