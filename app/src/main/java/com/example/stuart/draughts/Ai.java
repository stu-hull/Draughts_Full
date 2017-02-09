package com.example.stuart.draughts;

/**
 * Created by Stuart on 30/08/2016.
 * This is the Player class, of which two are created by the game object. They can be told to make a move, and if AI can then use minimax to choose a move itself.
 */

class Ai {

    //Version 1 of the minimax algorithm, this version will likely be replaced for greater efficiency and/or accuracy
    //Testing indicates this algorithm reach 8 plies in around 5 seconds of desktop processing time
    //Special features: none (no ab pruning, no quiscience search, no hash table, nothing)
    static double minimaxV1(Board board, boolean isBlack, int depth, Boolean optionalCapture){

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
            currentScore = minimaxV1(currentMove, !isBlack, depth - 1, optionalCapture); //score of current move is found with minimax

            //isBlack XOR the current score is lower than the best score; IE true if black and current score is greater than the best, or if white and the current score is lower
            if (isBlack != (currentScore < bestScore)){ //is this move the best so far for the current player?
                bestScore = currentScore;
            }
        }

        return bestScore; //return best score out available moves

    } //DONE //TESTED

    //Version 2 of the minimax algorithm, including alpha-beta pruning
    //This version can reach 9 plies slightly faster than V1 reaches 7 plies, which makes sense (7 * 4/3 = 9.3333)
    static double minimaxV2(Board board, boolean isBlack, int depth, boolean optionalCapture, double alpha, double beta){
        if (depth == 0){
            return heuristicV2(board);
        }

        Board[] availableMoves = board.findMoves(isBlack, optionalCapture);

        double bestScore;
        double currentScore; //the value of the current move

        if (isBlack) {
            bestScore = -Double.MAX_VALUE;
            for (Board move : availableMoves) {
                currentScore = minimaxV2(move, !isBlack, depth - 1, optionalCapture, alpha, beta);

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
                currentScore = minimaxV2(move, !isBlack, depth - 1, optionalCapture, alpha, beta);

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

    //the relative values of board positions and other figures
    private static final float baseValue = 100; //base value of every piece
    private static final float kingValue = 100; //kings are worth x more than base value
    private static final float backValue = 20; //pieces at the back are worth x more than base value
    private static final float centerValue = 0; //pieces in the center are worth x more than base value
    private static final float ratioConstant = 0; //changes the weight of a difference in piece count
    private static final float freePiece = 70; //pieces past all opposition which are guaranteed to become kings

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

        score *= (Long.bitCount(board.getBlackPieces()) + ratioConstant); //multiply the score by the ratio of black+constant : white+constant
        score /= (Long.bitCount(board.getWhitePieces()) + ratioConstant);

        return score;

    } //DONE //TESTED

    //Version 2 of the heurstic algorithm, taking into account runaway checkers
    private static double heuristicV2(Board board){

        double score = 0;

        score += Long.bitCount(board.getBlackPieces()) * baseValue;
        score -= Long.bitCount(board.getWhitePieces()) * baseValue;

        score += Long.bitCount(board.blackKings()) * kingValue;
        score -= Long.bitCount(board.whiteKings()) * kingValue;

        score += board.blackCount(Board.maskBlackBack) * backValue;
        score -= board.whiteCount(Board.maskWhiteBack) * backValue;

        score += board.blackCount(Board.maskCenter) * centerValue;
        score -= board.whiteCount(Board.maskCenter) * centerValue;

        score *= (Long.bitCount(board.getBlackPieces()) + ratioConstant); //multiply the score by the ratio of black+constant : white+constant
        score /= (Long.bitCount(board.getWhitePieces()) + ratioConstant);

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

    }

}