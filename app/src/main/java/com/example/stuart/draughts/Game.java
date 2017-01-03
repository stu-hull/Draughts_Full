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

    private Player player1;
    private Player player2;

    private Board currentBoard; //current board being displayed
    private Board newBoard; //board after move requested by player
    private Board[] legalMoves; //array of legal moves available
    private ImageView[] counterViews; //counter views, for adding into the GameActivity's RelativeLayout

    //boolean variables for details of the game
    private boolean againstComputer; //is it against AI or 2 player
    private boolean player1Black; //is player 1 black
    private boolean player1Turn; //is it player 1's turn

    private int highlighted = -1; //square to highlight on user interface (last one clicked), -1 means none highlighted
    private Boolean onlyMultiJump = false;

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
    ImageView[] getCounterViews() {
        return counterViews;
    }
    boolean isAgainstComputer() {
        return againstComputer;
    }
    private boolean isPlayer1Black() {
        return player1Black;
    }
    boolean isPlayer1Turn() {
        return player1Turn;
    }

    private int[][] displayCoordinates; //holds pixel coordinates for various points on the grid.

    //constructor sets up initial details of the game and displays them
    Game(int[][] displayCoordinates, boolean againstComputer, boolean player1Black) {

        player1 = new Player(player1Black, true); //set up player 1; they are always human
        player2 = new Player(!player1Black, !againstComputer); //set up player 2; can be human or AI

        this.againstComputer = againstComputer; //is the game against the computer? Which player is black?
        this.player1Black = player1Black;

        //create the board; first player to move is black
        currentBoard = new Board();
        legalMoves = currentBoard.findMoves(true);
        System.out.println(legalMoves.length);
        player1Turn = player1Black;

        counterViews = new ImageView[24]; //array of counters- cannot be set to starting layout until context is known

        this.displayCoordinates = displayCoordinates; //save coordinates
    }

    //creates ImageViews for counter positions, based on context of activity and coordinates of squares
    void updateCounterViews(Context context){

        int counterSize = (displayCoordinates[6][0] - displayCoordinates[5][0])/2;
        int counterIndex = 0;

        int[] counterIds = new int[]{ //array of every id to assign each counter
                R.id.counter0, R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5,
                R.id.counter6, R.id.counter7, R.id.counter8, R.id.counter9, R.id.counter10, R.id.counter11,
                R.id.counter12, R.id.counter13, R.id.counter14, R.id.counter15, R.id.counter16, R.id.counter17,
                R.id.counter18, R.id.counter19, R.id.counter20, R.id.counter21, R.id.counter22, R.id.counter23
        };

        int drawableId;

        for (int positionIndex = 0; positionIndex <= 40; positionIndex++) { //for each position

            //decide on image for counter
            if (positionIndex == highlighted && (currentBoard.isBlack(positionIndex) || currentBoard.isWhite(positionIndex))){
                if (currentBoard.isKing(positionIndex)) {
                    drawableId = R.drawable.manhighlighted;
                } else {
                    drawableId = R.drawable.kinghighlighted;
                }
            } else if (currentBoard.isBlack(positionIndex)){
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

    //allows user touch to be passed into game to figure out what to display to the user. The function also returns true if the user's turn is now complete, or false if they have to keep clicking
    Boolean userInput(int bit){
        if (againstComputer && !(player1Turn)){
            throw new IllegalStateException("userInput called when no user input is required");
        }
        if (highlighted == -1){ //if nothing highlighted
            System.out.println("1 Nothing already highlighted");
            if (!(currentBoard.isEmpty(bit)) && (currentBoard.isBlack(bit) == (isPlayer1Black() == player1Turn))){ //if tapped on counter and counter is right colour, highlight counter
                highlighted = bit;
                System.out.println("1.1 Counter highlighted");
            }
        } else {
            System.out.println("2 Something already highlighted");
            if (currentBoard.isEmpty(bit)) { //if tapped on empty space
                System.out.println("2.1 Tapped on clear space");
                newBoard = currentBoard.makeSimpleMove(highlighted, bit); //make move on new board
                Boolean legal = false;
                for (Board move : legalMoves) { //test if legal
                    if (move.getBlackPieces() == newBoard.getBlackPieces() && move.getWhitePieces() == newBoard.getWhitePieces() && move.getKings() == newBoard.getKings()) {
                        legal = true;
                    }
                }
                if (legal) {
                    System.out.println("2.1.1 Move requested is legal");
                    if ((highlighted - bit) * (highlighted - bit) <= 25) { //if a slide was made rather than a jump, make move and reset (unless in multijump, in which case do nothing)
                        System.out.println("2.1.1.1 Move requested is a slide");
                        if (!(onlyMultiJump)) {
                            System.out.println("2.1.1.1.1 Not in multijump- confirm move, reset, unhighlight");
                            currentBoard = newBoard;
                            highlighted = -1;
                            player1Turn = !player1Turn;
                            legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                            return true;
                        }
                    } else {
                        System.out.println("2.1.1.2 Move requested is a jump");
                        Board[] newNewBoards = newBoard.findMultiJump(bit); //find all further possible jumps
                        if (newNewBoards.length > 0) { //if more jumps available, make move, keep piece highlighted, and record that only multi jumps allowed
                            System.out.println("2.1.1.2.1 Move has further jump options- onlymultijump enabled, piece moved and highlighted");
                            currentBoard = newBoard;
                            legalMoves = currentBoard.findMultiJump(bit); //only legal moves are the multi jump ones
                            highlighted = bit;
                            onlyMultiJump = true;
                        } else { //if no further jumps, make move, change player to nextPlayer and reset
                            System.out.println("2.1.1.2.2 Move has no further jumps- confirm move, reset and unhighlight");
                            currentBoard = newBoard;
                            highlighted = -1;
                            player1Turn = !player1Turn;
                            onlyMultiJump = false;
                            legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                            return true;
                        }
                    }
                } else {
                    System.out.println("2.1.2 Move requested is illegal");
                    if (!onlyMultiJump) {
                        System.out.println("2.1.2.1 Not in multijump- unhighlight");
                        highlighted = -1; //if illegal, reset (ignore if in multijump)
                    }
                }
            } else if (onlyMultiJump){
                System.out.println("2.2 Tapped on counter but in multijump");
                if (bit == highlighted) { //if doing a multijump and the player taps the piece, stop the jump and end the turn (if they tap another piece, ignore
                    System.out.println("2.2.1 Tapped on highlighted counter, stop jump");
                    highlighted = -1;
                    player1Turn = !player1Turn;
                    onlyMultiJump = false;
                    legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                    return true;
                }
            } else {
                System.out.println("2.3 Tapped on counter, unhighlight");
                highlighted = -1; //else reset
            }
        }
        return false;
    }

}