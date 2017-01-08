package com.example.stuart.draughts;

// ANNOTATION TERMINOLOGY:
// Mover- a piece which can slide into an adjacent square
// Jumper- a piece which can jump an enemy piece into the square beyond
// LF, RF, LB, RB - Left-forward, right-forward, right-backward, left-backward (from Black's perspective)
// Bitboard- a Long number, 45 bits used to represent some boolean property of every square of a game board, eg, all of the squares with white pieces on
// Mask- like a bitboard, but representing a boolean property universal to all boards, eg, the 32 out of 45 bits which represent valid board locations

// 'currentBoard' refers ONLY to the current gamestate being displayed to the player. For all other hypothetical boards please use 'board'

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Game {

    private Board currentBoard; //current board being displayed
    private Board[] legalMoves; //array of legal moves available

    //boolean variables for details of the game
    private boolean againstComputer; //is it against AI or 2 player
    private boolean player1Black; //is player 1 black
    public boolean player1Turn; //is it player 1's turn

    //getters for all of those
    public Board getCurrentBoard() {
        return currentBoard;
    }
    public Board[] getLegalMoves(){
        return legalMoves;
    }
    public boolean isAgainstComputer() {
        return againstComputer;
    }
    public boolean isPlayer1Black() {
        return player1Black;
    }
    public boolean isPlayer1Turn() {
        return player1Turn;
    }

    //constructor sets up initial details of the game and displays them
    Game( boolean againstComputer, boolean player1Black) {
        this.againstComputer = againstComputer; //is the game against the computer? Which player is black?
        this.player1Black = player1Black;

        //create the board; first player to move is black
        currentBoard = new Board();
        legalMoves = currentBoard.findMoves(true);
        player1Turn = player1Black;
    }

    //update currentBoard, and start new thread for AI to make a move if necessary
    public void setCurrentBoard(Board newBoard, Boolean onlyMultiJump, int highlighted){
        currentBoard = newBoard;
        if (!onlyMultiJump){
            player1Turn = !player1Turn;
            legalMoves = currentBoard.findMoves(player1Turn == player1Black);
        } else {
            legalMoves = currentBoard.findMultiJump(highlighted);
        }
    }



}