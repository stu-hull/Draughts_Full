package com.example.stuart.draughts;

import android.content.Intent;
import android.net.Uri;
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

    //Runs when "Start Game" button on home screen is pressed- starts game activity, passes settings to activity
    public void startGame(View view) {
        Intent intent = new Intent(MainMenu.this, GameActivity.class);
        intent.putExtra("againstComputer", againstComputer);
        intent.putExtra("optionalCapture", optionalCapture);
        intent.putExtra("player1Colour", player1Colour);
        intent.putExtra("player2Colour", player2Colour);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    public void startSettings(View view){
        Intent intent = new Intent(MainMenu.this, Settings.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                againstComputer = data.getExtras().getBoolean("againstComputer");
                optionalCapture = data.getExtras().getBoolean("optionalCapture");
                if (data.getExtras().getBoolean("defaultColours", true)){
                    player1Colour = 1;
                    player2Colour = 0;
                } else {
                    player1Colour = data.getExtras().getInt("player1Colour", 1);
                    player2Colour = data.getExtras().getInt("player2Colour", 0);
                }
                difficulty = data.getExtras().getInt("difficulty");
            }
        }
    }

}
