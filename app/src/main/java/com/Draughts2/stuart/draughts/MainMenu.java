package com.Draughts2.stuart.draughts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Boolean againstComputer;
    Boolean optionalCapture;
    int player1Colour;
    int player2Colour;
    int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        againstComputer = false;
        player1Colour = 1;
        player2Colour = 0;
        difficulty = 1;
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long blackPieces = preferences.getLong("blackPieces", -1);
        Button loadGame = (Button) findViewById(R.id.load_game); //check to see if there is game to load, if not, grey out button
        if (blackPieces == -1){
            loadGame.setAlpha(.5f);
            loadGame.setClickable(false);
        } else {
            loadGame.setAlpha(1f);
            loadGame.setClickable(true);
        }
    }

    public void startSettings(View view){
        Intent intent = new Intent(MainMenu.this, Settings.class);
        startActivityForResult(intent, 1);
    }

    public void loadGame(View view){
        Intent intent = new Intent(MainMenu.this, Settings.class);
        intent.putExtra("loadGame", true);
        System.out.println("PRESSED LOADGAME");
        startActivityForResult(intent, 1);
    }

}
