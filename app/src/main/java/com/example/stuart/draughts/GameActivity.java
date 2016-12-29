package com.example.stuart.draughts;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
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
        float boardSize = (metrics.widthPixels - 50*metrics.density); //size is width of screen minus 50dp (dp is converted to px by multiplying by density)
        float square0width = (metrics.widthPixels - boardSize)/2; //first column from the left
        float square0height = (metrics.heightPixels + boardSize*1/2)/2;// + boardSize*7/16); //first row up from bottom

        System.out.println(boardSize);
        System.out.println(metrics.widthPixels);
        System.out.println(metrics.heightPixels);
        System.out.println(metrics.density);
        System.out.println(square0width);
        System.out.println(square0height);

        //Set up relativelayout
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(getResources().getColor(R.color.background));

        //Set up game board
        ImageView gameBoard = new ImageView(this);
        gameBoard.setId(R.id.gameBoard);
        gameBoard.setImageResource(R.drawable.boardv2);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                (int)(boardSize),
                (int)(boardSize));
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

        //set up first counter
        ImageView counter0 = new ImageView(this);
        counter0.setId(R.id.counter0);
        counter0.setImageResource(R.drawable.redman);
        RelativeLayout.LayoutParams counter0Params = new RelativeLayout.LayoutParams(
                (int) ((float)boardSize/8),
                (int) ((float)boardSize/8));
        counter0Params.leftMargin = (int)(square0width);
        counter0Params.topMargin = (int)(square0height);

        //add all views into layout
        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        layout.addView(player2Label, player2LabelParams);
        layout.addView(counter0, counter0Params);

        //show layout
        setContentView(layout);

        System.out.println("DONE");
    }

}
