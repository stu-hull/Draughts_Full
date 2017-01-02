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

    private Player player1;
    private Player player2;

    public Board currentBoard; //current board being displayed
    private Board newBoard; //board after move requested by player
    private ImageView[] counterViews; //counter views, for adding into the GameActivity's RelativeLayout

    //boolean variables for details of the game
    private boolean againstComputer; //is it against AI or 2 player
    private boolean player1Black; //is player 1 black
    private boolean player1Turn; //is it player 1's turn

    //getters for all of those
    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public Board getCurrentBoard() {
        return currentBoard;
    }
    public Board getNewBoard() {
        return newBoard;
    }
    public ImageView[] getCounterViews() {
        return counterViews;
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

    public int[][] displayCoordinates; //holds pixel coordinates for various points on the grid.

    //constructor sets up initial details of the game and displays them
    public Game(int[][] displayCoordinates, boolean againstComputer, boolean player1Black) {

        player1 = new Player(player1Black, true); //set up player 1; they are always human
        player2 = new Player(!player1Black, !againstComputer); //set up player 2; can be human or AI

        this.againstComputer = againstComputer; //is the game against the computer? Which player is black?
        this.player1Black = player1Black;

        //create the board; first player to move is black
        currentBoard = new Board();
        player1Turn = player1Black;

        counterViews = new ImageView[24]; //array of counters- cannot be set to starting layout until context is known

        this.displayCoordinates = displayCoordinates; //save coordinates
    }

    //creates ImageViews for counter positions, based on context of activity and coordinates of squares
    public void updateCounterViews(Context context){

        int counterSize = (displayCoordinates[6][0] - displayCoordinates[5][0])/2;
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
            counterViews[counterIndex] = counter;
            counterIndex++;

        }

    }

    public void counterAt(float x, float y){
        for (ImageView counter : counterViews){
            System.out.println(counter.getLayoutParams().height);
            System.out.println(counter.getLayoutParams().width);
        }
    }

}