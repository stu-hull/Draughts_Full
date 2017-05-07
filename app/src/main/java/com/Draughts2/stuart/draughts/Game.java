package com.Draughts2.stuart.draughts;

// ANNOTATION TERMINOLOGY:
// Mover- a piece which can slide into an adjacent square
// Jumper- a piece which can jump an enemy piece into the square beyond
// LF, RF, LB, RB - Left-forward, right-forward, right-backward, left-backward (from Black's perspective)
// Bitboard- a Long number, 45 bits used to represent some boolean property of every square of a game board, eg, all of the squares with white pieces on
// Mask- like a bitboard, but representing a boolean property universal to all boards, eg, the 32 out of 45 bits which represent valid board locations

// 'currentBoard' refers ONLY to the current gamestate being displayed to the player. For all other hypothetical boards please use 'board'

class Game{

    private Board currentBoard; //current board being displayed
    private Board[] legalMoves; //array of legal moves available
    boolean inMultiJump = false;
    private Board temporaryBoard;
    private Board[] temporaryLegalMoves;

    //boolean variables for details of the game
    private boolean againstComputer; //is it against AI or 2 player
    private boolean optionalCapture;
    private boolean player1Turn; //is it player 1's turn

    private Game previousGameState; //previous game state to undo from

    //getters for all of those
    Board getCurrentBoard() {
        if (inMultiJump){ //if in multijump, move has not finished so return temporary board
            return temporaryBoard;
        }
        return currentBoard;
    }
    Board[] getLegalMoves(){
        if (inMultiJump){ //if in multijump, only jumps available so return temporary legal moves
            return temporaryLegalMoves;
        }
        return legalMoves;
    }
    boolean isAgainstComputer() {
        return againstComputer;
    }
    boolean isOptionalCapture() {
        return optionalCapture;
    }
    boolean isPlayer1Turn() {
        return player1Turn;
    }
    Game getPreviousGameState() {
        return previousGameState;
    }

    //constructor sets up initial details of the game and displays them
    Game(boolean againstComputer, boolean optionalCapture) {
        this.againstComputer = againstComputer; //is the game against the computer? Which player is black?
        this.optionalCapture = optionalCapture;
        player1Turn = true;

        //create the board; first player to move is black
        currentBoard = new Board();
        legalMoves = currentBoard.findMoves(true, optionalCapture);
    }

    //creates a copy of a game from another game
    Game(Game anotherGame){
        this.againstComputer = anotherGame.isAgainstComputer();
        this.optionalCapture = anotherGame.isOptionalCapture();
        this.player1Turn = anotherGame.isPlayer1Turn();
        this.currentBoard = new Board(anotherGame.getCurrentBoard());
        this.legalMoves = new Board[anotherGame.getLegalMoves().length];
        for (int i=0; i<legalMoves.length; i++){
            this.legalMoves[i] = new Board(anotherGame.getLegalMoves()[i]);
        }
        if (anotherGame.getPreviousGameState() != null) {
            this.previousGameState = new Game(anotherGame.getPreviousGameState());
        } else {
            this.previousGameState = null;
        }
    }

    //update currentBoard
    void setCurrentBoard(Board newBoard, int highlighted){
        if (inMultiJump){
            temporaryBoard = newBoard;
            temporaryLegalMoves = temporaryBoard.findMultiJump(highlighted);
        } else {
            currentBoard = newBoard;
            player1Turn = !player1Turn;
            legalMoves = currentBoard.findMoves(player1Turn, optionalCapture);
        }
    }

    //save current game state to undo to
    void saveGame(){
        previousGameState = new Game(this);
    }

}