package com.example.stuart.draughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void startGame(View view) {
        startActivityForResult(new Intent(MainMenu.this, Game.class), 1);
        boolean againstComputer = true;
        boolean player1black = true;
        Game game = new Game(againstComputer, player1black);
        game.runGame();
    }

}
