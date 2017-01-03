package com.example.stuart.draughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

public class MainMenu extends AppCompatActivity {

    Boolean againstComputer;
    Boolean player1Black;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        againstComputer = false;
        player1Black = true;
    }

    //Runs when "Start Game" button on home screen is pressed- starts game activity, passes settings to activity
    public void startGame(View view) {
        Intent intent = new Intent(MainMenu.this, GameActivity.class);
        intent.putExtra("AGAINST_COMPUTER", againstComputer);
        intent.putExtra("PLAYER_1_BLACK", player1Black);
        startActivity(intent);
    }

}
