package com.example.stuart.draughts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //find dimensions dynamically
        DisplayMetrics metrics = getResources().getDisplayMetrics(); //metrics holds information about the display
        int boardSize = (int)(metrics.widthPixels - 50*metrics.density); //size is width of screen minus 50dp (dp is converted to px by multiplying by density)
        int square0width = (int)((metrics.widthPixels - boardSize)/2); //first column from the left
        int square0height = (int)((metrics.heightPixels + boardSize*1/2)/2);// + boardSize*7/16); //first row up from bottom
        int squareSize = (int)(boardSize/8);

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

        //set up black counters
        ImageView[] blackCounters = new ImageView[12]; //array of ImageViews
        int[] blackIds = new int[]{ //array of every id to assign each counter
                R.id.counter0, R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5,
                R.id.counter6, R.id.counter7, R.id.counter8, R.id.counter9, R.id.counter10, R.id.counter11,
        };
        int counterIndex = 0;
        for (int positionIndex = 0; positionIndex < 18; positionIndex++){ //counts through positions up to first 3 lines
            if ((Board.maskValid & 1L<<(long)(45-positionIndex)) != 0L){ //if position is valid
                ImageView counter = new ImageView(this); //set up counter, similar to other setups
                counter.setId(blackIds[counterIndex]);
                counter.setImageResource(R.drawable.redman);
                RelativeLayout.LayoutParams counterParams = new RelativeLayout.LayoutParams(
                        squareSize,
                        squareSize
                );
                counterParams.leftMargin = coordinates[positionIndex][0]; //margins are fetched from coordinates array
                counterParams.topMargin = coordinates[positionIndex][1];

                counter.setLayoutParams(counterParams); //add params into view, so only managing one object
                blackCounters[counterIndex] = counter; //add to array of ImageViews
                counterIndex++;
            }
        }

        //set up white counters
        ImageView[] whiteCounters = new ImageView[12]; //array of ImageViews
        int[] whiteIds = new int[]{ //array of every id to assign each counter
                R.id.counter12, R.id.counter13, R.id.counter14, R.id.counter15, R.id.counter16, R.id.counter17,
                R.id.counter18, R.id.counter19, R.id.counter20, R.id.counter21, R.id.counter22, R.id.counter23
        };
        counterIndex = 0;
        for (int positionIndex = 28; positionIndex < 45; positionIndex++){ //counts through positions of last 3 lines
            if ((Board.maskValid & 1L<<(long)(45-positionIndex)) != 0L) { //if position is valid
                ImageView counter = new ImageView(this); //set up counter, similar to other setups
                counter.setId(whiteIds[counterIndex]);
                counter.setImageResource(R.drawable.whiteman);
                RelativeLayout.LayoutParams counterParams = new RelativeLayout.LayoutParams(
                        squareSize,
                        squareSize
                );
                counterParams.leftMargin = coordinates[positionIndex][0]; //margins are fetched from coordinates array
                counterParams.topMargin = coordinates[positionIndex][1];

                counter.setLayoutParams(counterParams); //add params into view, so only managing one object
                whiteCounters[counterIndex] = counter; //add to array of ImageViews
                counterIndex++;
            }
        }

        //add all views into layout
        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        layout.addView(player2Label, player2LabelParams);
        for (ImageView counter : blackCounters){
            layout.addView(counter);
        }
        for (ImageView counter : whiteCounters){
            layout.addView(counter);
        }

        //show layout
        setContentView(layout);

        System.out.println("DONE");
    }

}
