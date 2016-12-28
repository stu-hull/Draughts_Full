package com.example.stuart.draughts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        System.out.println("0");

        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(getResources().getColor(R.color.background));

        ImageView gameBoard = new ImageView(this);
        gameBoard.setId(R.id.gameBoard);
        gameBoard.setImageResource(R.drawable.boardv2);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                (int) (metrics.widthPixels - 50*metrics.density),
                (int) (metrics.widthPixels - 50*metrics.density));
        gameBoardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameBoardParams.addRule(RelativeLayout.CENTER_VERTICAL);

        System.out.println(metrics.density);

        TextView player1Label = new TextView(this);
        player1Label.setId(R.id.player1Label);
        player1Label.setText(R.string.player1);
        player1Label.setTextSize(30);
        RelativeLayout.LayoutParams player1LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player1LabelParams.addRule(RelativeLayout.BELOW, gameBoard.getId());
        player1LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        System.out.println("2");

        TextView player2Label = new TextView(this);
        player2Label.setId(R.id.player2Label);
        player2Label.setText(R.string.player2);
        player2Label.setTextSize(30);
        RelativeLayout.LayoutParams player2LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player2LabelParams.addRule(RelativeLayout.ABOVE, gameBoard.getId());
        player2LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        System.out.println("3");

        ImageView counter0 = new ImageView(this);
        counter0.setId(R.id.counter0);
        counter0.setImageResource(R.drawable.redman);

        RelativeLayout.LayoutParams counter0Params = new RelativeLayout.LayoutParams(
                (int) ((metrics.widthPixels - 50*metrics.density)/8),
                (int) ((metrics.widthPixels - 50*metrics.density)/8));
        counter0Params.leftMargin = 100;
        counter0Params.topMargin = 150;

        System.out.println("4");

        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        layout.addView(player2Label, player2LabelParams);
        layout.addView(counter0, counter0Params);
        setContentView(layout);

        System.out.println("DONE");
    }

}
