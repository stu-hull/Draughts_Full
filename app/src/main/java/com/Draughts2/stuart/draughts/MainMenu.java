package com.Draughts2.stuart.draughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
