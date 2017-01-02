package com.example.stuart.draughts;

// ANNOTATION TERMINOLOGY:
// Mover- a piece which can slide into an adjacent square
// Jumper- a piece which can jump an enemy piece into the square beyond
// LF, RF, LB, RB - Left-forward, right-forward, right-backward, left-backward (from Black's perspective)
// Bitboard- a Long number, 45 bits used to represent some boolean property of every square of a game board, eg, all of the squares with white pieces on
// Mask- like a bitboard, but representing a boolean property universal to all boards, eg, the 32 out of 45 bits which represent valid board locations

// 'currentBoard' refers ONLY to the current gamestate being displayed to the player. For all other hypothetical boards please use 'board'

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    public Player player1;
    public Player player2;

    public Board currentBoard; //current board being displayed
    public Board newBoard; //board after move requested by player

    //boolean variables for details of the game
    public boolean againstComputer; //is it against AI or 2 player
    public boolean player1Black; //is player 1 black
    public boolean player1Turn; //is it player 1's turn

    public int[][] displayCoordinates; //holds pixel coordinates for various points on the grid.

    //default constructor, againstCOmputer and player1Black both true
    public Game(){
        //is the game against the computer? Which player is black?
        this.againstComputer = true;
        this.player1Black = true;

        //set up player 1; they are always human
        player1 = new Player(player1Black, true);

        //set up player 2; can be human or AI
        player2 = new Player(!player1Black, !againstComputer);

        //create the board; first player to move is black
        currentBoard = new Board();
        player1Turn = player1Black;

        //shows start pieces on board
        displayMove();
    }

    //constructor sets up initial details of the game and displays them
    public Game(int[][] displayCoordinates, boolean againstComputer, boolean player1Black) {

        //save coordinates
        this.displayCoordinates = displayCoordinates;

        //is the game against the computer? Which player is black?
        this.againstComputer = againstComputer;
        this.player1Black = player1Black;

        //set up player 1; they are always human
        player1 = new Player(player1Black, true);

        //set up player 2; can be human or AI
        player2 = new Player(!player1Black, !againstComputer);

        //create the board; first player to move is black
        currentBoard = new Board();
        player1Turn = player1Black;

        //shows start pieces on board
        //displayMove();
    }

    //uses currentBoard and newBoard to animate a move taking place.
    public void displayMove(){

    }

    //creates ImageViews for counter positions, based on context of activity and coordinates of squares
    public ImageView[] findCounterViews(Context context){

        int counterSize = (displayCoordinates[6][0] - displayCoordinates[5][0])/2;

        ImageView[] counters = new ImageView[24];
        int counterIndex = 0;

        int[] counterIds = new int[]{ //array of every id to assign each counter
                R.id.counter0, R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5,
                R.id.counter6, R.id.counter7, R.id.counter8, R.id.counter9, R.id.counter10, R.id.counter11,
                R.id.counter12, R.id.counter13, R.id.counter14, R.id.counter15, R.id.counter16, R.id.counter17,
                R.id.counter18, R.id.counter19, R.id.counter20, R.id.counter21, R.id.counter22, R.id.counter23
        };

        int drawableId;

        for (int positionIndex = 0; positionIndex <= 40; positionIndex++){ //for each position

            //decide on image for counter
            if (currentBoard.isBlack(positionIndex)){
                if (currentBoard.isKing(positionIndex)){
                    drawableId = R.drawable.redking;
                } else {
                    drawableId = R.drawable.redman;
                }
            } else if (currentBoard.isWhite(positionIndex)) {
                if (currentBoard.isKing(positionIndex)){
                    drawableId = R.drawable.whiteking;
                } else {
                    drawableId = R.drawable.whiteman;
                }
            } else {
                continue; //if not black or white, continue loop
            }

            //create counter, assign ID & drawable
            ImageView counter = new ImageView(context);
            counter.setId(counterIds[counterIndex]);
            counter.setImageResource(drawableId);

            RelativeLayout.LayoutParams counterParams = new RelativeLayout.LayoutParams(
                    counterSize,
                    counterSize
            );

            counterParams.leftMargin = displayCoordinates[positionIndex][0]; //margins are fetched from coordinates array
            counterParams.topMargin = displayCoordinates[positionIndex][1];

            counter.setLayoutParams(counterParams);
            counters[counterIndex] = counter;
            counterIndex++;

        }

        return counters;
    }

}