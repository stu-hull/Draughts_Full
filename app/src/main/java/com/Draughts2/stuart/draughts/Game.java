package com.Draughts2.stuart.draughts;

// ANNOTATION TERMINOLOGY:
// Mover- a piece which can slide into an adjacent square
// Jumper- a piece which can jump an enemy piece into the square beyond
// LF, RF, LB, RB - Left-forward, right-forward, right-backward, left-backward (from Black's perspective)
// Bitboard- a Long number, 45 bits used to represent some boolean property of every square of a game board, eg, all of the squares with white pieces on
// Mask- like a bitboard, but representing a boolean property universal to all boards, eg, the 32 out of 45 bits which represent valid board locations

// 'currentBoard' refers ONLY to the current gamestate being displayed to the player. For all other hypothetical boards please use 'board'

import android.os.Parcel;
import android.os.Parcelable;

class Game implements Parcelable{

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

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

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

    //creates game from a parcel
    protected Game(Parcel in) {
        //read data from arrays
        boolean[] booleanData = new boolean[3];
        in.readBooleanArray(booleanData);
        long[] longData = new long[3];
        in.readLongArray(longData);

        //save data from arrays
        inMultiJump = false;
        againstComputer = booleanData[0];
        optionalCapture = booleanData[1];
        player1Turn = booleanData[2];
        currentBoard = new Board(longData[0], longData[1], longData[2]);

        //recalculate legal moves
        legalMoves = currentBoard.findMoves(player1Turn, optionalCapture);

    }

    Game(Board board, boolean againstComputer, boolean optionalCapture, boolean player1Turn){
        this.againstComputer = againstComputer; //is the game against the computer? Which player is black?
        this.optionalCapture = optionalCapture;
        this.player1Turn = player1Turn;

        //create the board
        currentBoard = board;
        legalMoves = currentBoard.findMoves(player1Turn, optionalCapture);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeBooleanArray(new boolean[]{againstComputer, optionalCapture, player1Turn});
        out.writeLongArray(new long[]{currentBoard.getBlackPieces(), currentBoard.getWhitePieces(), currentBoard.getKings()});
    }
}