package com.example.stuart.draughts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //find dimensions dynamically
        DisplayMetrics metrics = getResources().getDisplayMetrics(); //metrics holds information about the display
        int boardSize = (int)(metrics.widthPixels - 50*metrics.density); //size is width of screen minus 50dp (dp is converted to px by multiplying by density)
        int square0width = ((metrics.widthPixels - boardSize)/2); //first column from the left
        int square0height = ((metrics.heightPixels + boardSize/2)/2); //first row up from bottom
        int squareSize = (boardSize/8);
        System.out.println(boardSize);
        System.out.println(square0width);
        System.out.println(square0height);
        System.out.println(squareSize);

        //Set up relativelayout
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(getResources().getColor(R.color.background));

        //Set up game board
        ImageView gameBoard = new ImageView(this);
        gameBoard.setId(R.id.gameBoard);
        gameBoard.setImageResource(R.drawable.boardv6);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                boardSize,
                boardSize);
        gameBoardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameBoardParams.addRule(RelativeLayout.CENTER_VERTICAL);

        //set up player 1 label
        TextView player1Label = new TextView(this);
        player1Label.setId(R.id.player1Label);
        player1Label.setText(R.string.player1);
        player1Label.setTextSize(30);
        RelativeLayout.LayoutParams player1LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player1LabelParams.addRule(RelativeLayout.BELOW, gameBoard.getId());
        player1LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //set up player 2 label
        TextView player2Label = new TextView(this);
        player2Label.setId(R.id.player2Label);
        player2Label.setText(R.string.player2);
        player2Label.setTextSize(30);
        RelativeLayout.LayoutParams player2LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player2LabelParams.addRule(RelativeLayout.ABOVE, gameBoard.getId());
        player2LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //coordinates of each square on the board, in pixels
        int[][] coordinates = new int[][]{
                {0,0}, {0,0}, {0,0}, {0,0}, {0,0},
                {square0width, square0height}, {square0width+2*squareSize, square0height},  {square0width+4*squareSize, square0height},  {square0width+6*squareSize, square0height}, {0,0},
                {square0width+squareSize, square0height-squareSize}, {square0width+3*squareSize, square0height-squareSize}, {square0width+5*squareSize, square0height-squareSize}, {square0width+7*squareSize, square0height-squareSize},
                {square0width, square0height-2*squareSize}, {square0width+2*squareSize, square0height-2*squareSize},  {square0width+4*squareSize, square0height-2*squareSize},  {square0width+6*squareSize, square0height-2*squareSize}, {0,0},
                {square0width+squareSize, square0height-3*squareSize}, {square0width+3*squareSize, square0height-3*squareSize}, {square0width+5*squareSize, square0height-3*squareSize}, {square0width+7*squareSize, square0height-3*squareSize},
                {square0width, square0height-4*squareSize}, {square0width+2*squareSize, square0height-4*squareSize},  {square0width+4*squareSize, square0height-4*squareSize},  {square0width+6*squareSize, square0height-4*squareSize}, {0,0},
                {square0width+squareSize, square0height-5*squareSize}, {square0width+3*squareSize, square0height-5*squareSize}, {square0width+5*squareSize, square0height-5*squareSize}, {square0width+7*squareSize, square0height-5*squareSize},
                {square0width, square0height-6*squareSize}, {square0width+2*squareSize, square0height-6*squareSize},  {square0width+4*squareSize, square0height-6*squareSize},  {square0width+6*squareSize, square0height-6*squareSize}, {0,0},
                {square0width+squareSize, square0height-7*squareSize}, {square0width+3*squareSize, square0height-7*squareSize}, {square0width+5*squareSize, square0height-7*squareSize}, {square0width+7*squareSize, square0height-7*squareSize},
                {0,0}, {0,0}, {0,0}, {0,0}, {0,0}
        };

        game = new Game(coordinates, true, true);

        ImageView[] counters = game.findCounterViews(this);

        //add all views into layout
        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        layout.addView(player2Label, player2LabelParams);
        for (ImageView counter : counters){
            if (counter == null){
                break;
            }
            layout.addView(counter);
        }

        //show layout
        setContentView(layout);

        System.out.println("DONE");
    }



}
