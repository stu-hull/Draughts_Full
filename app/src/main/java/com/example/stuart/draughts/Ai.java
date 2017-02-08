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

    static double minimaxV2(Board board, boolean isBlack, int depth, boolean optionalCapture, double alpha, double beta){
        if (depth == 0){
            return heuristicV1(board);
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

    public static void main(String[] args){
        double x = mini(true, 4, false, -Double.MAX_VALUE, Double.MAX_VALUE);
        System.out.println("Final result: " + Double.toString(x));
    }

    static double mini(boolean isBlack, int depth, Boolean optionalCapture, double alpha, double beta){
        System.out.println("Starting algorithm at layer " + Integer.toString(depth) + " with a and b values of " + Double.toString(alpha) + " and " + Double.toString(beta));
        if (depth == 0){
            double value = p();
            System.out.println("Depth = 0; returning heuristic value of: " + Double.toString(value));
            return value;
        }

        double currentScore; //the value of the current move
        if (isBlack) {
            double bestScore = -Double.MAX_VALUE;
            for (int i = 0; i < 2; i++) {
                System.out.println("Looking at move " + Integer.toString(i));
                currentScore = mini(!isBlack, depth - 1, optionalCapture, alpha, beta);
                if (currentScore > bestScore){
                    System.out.println("Best on this node so far");
                    bestScore = currentScore;
                    if (bestScore > alpha) {
                        System.out.println("Best for maximiser so far");
                        alpha = bestScore;
                        if (alpha >= beta) {
                            System.out.println("Cutoff, returning bestScore = " + Double.toString(bestScore));
                            break;
                        }
                    }
                }
            }
            System.out.println("Execution complete, returning value of: " + Double.toString(bestScore));
            return bestScore;

        } else {
            double bestScore = Double.MAX_VALUE;
            for (int i = 0; i < 2; i++) {
                System.out.println("Looking at move " + Integer.toString(i));
                currentScore = mini(!isBlack, depth - 1, optionalCapture, alpha, beta);
                if (currentScore < bestScore) {
                    System.out.println("Best on this node so far");
                    bestScore = currentScore;
                    if (bestScore < beta) {
                        System.out.println("Best for minimiser so far");
                        beta = bestScore;
                        if (alpha >= beta) {
                            System.out.println("Cutoff, returning bestScore = " + Double.toString(bestScore));
                            break;
                        }
                    }
                }
            }
            System.out.println("Execution complete, returning value of: " + Double.toString(bestScore));
            return bestScore;
        }

    }

    private static int i = -1;
    private static double p(){
        int[] array = new int[]{10, 11, 9, 14, 15, 5, 4};
        i++;
        return array[i];
    }


    //Version 1 of the heuristic algorithm, this version will likely be replaced for greater efficiency and/or accuracy
    //Ideas: incentives to keep back row intact, control central 8 squares, and get kings; value a piece difference greater with fewer pieces on board; blocking option?
    //MAYBE use a genetic algorithm to optimise these values?
    private static double heuristicV1(Board board){

    //the relative values of board positions and other figures
    double baseValue = 100; //base value of every piece
    double kingValue = 100; //kings are worth x more than base value
    double backValue = 20; //pieces at the back are worth x more than base value
    double centerValue = 0; //pieces in the center are worth x more than base value
    double ratioConstant = 0; //changes the weight of a difference in piece count

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

}