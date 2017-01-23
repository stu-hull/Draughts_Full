package com.example.stuart.draughts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    Boolean againstComputer;
    int player1Colour;
    int player2Colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        againstComputer = false;
        player1Colour = 1;
        player2Colour = 0;
    }

    //Runs when "Start Game" button on home screen is pressed- starts game activity, passes settings to activity
    public void startGame(View view) {
        Intent intent = new Intent(MainMenu.this, GameActivity.class);
        intent.putExtra("againstComputer", againstComputer);
        intent.putExtra("player1Colour", player1Colour);
        intent.putExtra("player2Colour", player2Colour);
        startActivity(intent);
    }

    public void startSettings(View view){
        Intent intent = new Intent(MainMenu.this, Settings.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("1");
        System.out.println("--- -Y- ---");
        System.out.println(requestCode);
        System.out.println(resultCode);
        System.out.println(RESULT_OK);
        System.out.println("--- --- ---");
        if (requestCode == 1){
            System.out.println("2");
            if (resultCode == RESULT_OK){
                System.out.println("3");
                againstComputer = data.getExtras().getBoolean("againstComputer");
                if (data.getExtras().getBoolean("defaultColours", true)){
                    player1Colour = 1;
                    player2Colour = 0;
                } else {
                    player1Colour = data.getExtras().getInt("player1Colour", 1);
                    player2Colour = data.getExtras().getInt("player2Colour", 0);
                }
            }
        }
    }

}
