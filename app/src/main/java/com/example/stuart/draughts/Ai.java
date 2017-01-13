package com.example.stuart.draughts;

/**
 * Created by Stuart on 30/08/2016.
 * This is the Player class, of which two are created by the game object. They can be told to make a move, and if AI can then use minimax to choose a move itself.
 */
class Ai {

    //This is the algorithm called by the AI, it actually runs the real algorithm on each move available and then chooses the best (the real minimax returns a value, not the best move)
    static Board minimax(Board currentBoard, boolean isBlack, int depth){

        Board[] availableMoves = currentBoard.findMoves(isBlack); //find all the moves available to the player

        Board bestMove = new Board(); //the best possible move
        bestMove.nullBoard(); //if no moves are available, this empty board will be returned and the Game will recognise this as a surrender
        double bestScore; //the value of the best possible move
        double currentScore; //the value of the current move

        if (isBlack) { //if player is black, high scores are good, so default score is as low as possible
            bestScore = Double.NEGATIVE_INFINITY;
        } else { //if player white, low scores are good, so default score is as high as possible
            bestScore = Double.POSITIVE_INFINITY;
        }

        for (Board currentMove : availableMoves) { //for each move in the list of possible moves
            currentScore = minimaxV1(currentMove, isBlack, depth-1); //score of current move is found with minimax
            if ((isBlack && currentScore > bestScore) || (!isBlack && currentScore < bestScore)){ //if current move is better than all before it (or worse if you're white)
                bestScore = currentScore; //set best score to score of current move
                bestMove = currentMove; //record the current move to be returned (this is the bit the other algorithm won't do)
            }
        }

        return bestMove;
    } //DONE //TESTED

    //Version 1 of the minimax algorithm, this version will likely be replaced for greater efficiency and/or accuracy
    //Testing indicates this algorithm reach 8 plies in around 5 seconds of desktop processing time
    //Special features: none (no ab pruning, no quiscience search, no hash table, nothing)
    private static double minimaxV1(Board board, boolean isBlack, int depth){

        if (depth == 0){ //if maximum depth is achieved, do not search further; perform the heuristic function
            return heuristicV1(board);
        }

        Board[] availableMoves = board.findMoves(isBlack);

        double bestScore; //the value of the best possible move
        double currentScore; //the value of the current move

        if (isBlack){ //if player is black, high scores are good, so default score is as low as possible
            bestScore = Double.NEGATIVE_INFINITY;
        } else { //if player white, low scores are good, so default score is as high as possible
            bestScore = Double.POSITIVE_INFINITY;
        }

        for (Board currentMove : availableMoves) { //for each move in the list of possible moves
            currentScore = minimaxV1(currentMove, isBlack, depth - 1); //score of current move is found with minimax

            //isBlack XOR the current score is lower than the best score; IE true if black and current score is greater than the best, or if white and the current score is lower
            if (isBlack != (currentScore < bestScore)){ //is this move the best so far for the current player?
                bestScore = currentScore;
            }
        }

        return bestScore; //return best score out available moves

    } //DONE //TESTED

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

//test change