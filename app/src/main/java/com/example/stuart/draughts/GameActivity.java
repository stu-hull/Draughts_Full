package com.example.stuart.draughts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(Color.BLUE);

        ImageView gameBoard = new ImageView(this);
        gameBoard.setImageResource(R.drawable.boardv2);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        gameBoardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameBoardParams.addRule(RelativeLayout.CENTER_VERTICAL);

        TextView player1Label = new TextView(this);
        player1Label.setText(R.string.player1);
        RelativeLayout.LayoutParams player1LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player1LabelParams.addRule(RelativeLayout.ABOVE, gameBoard.getId());
        player1LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        setContentView(layout);
        System.out.println("DONE");
    }
}
