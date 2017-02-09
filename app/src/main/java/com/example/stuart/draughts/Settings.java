package com.example.stuart.draughts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void finish(){
        Intent intent = new Intent();

        Switch computerToggle = (Switch) findViewById(R.id.computer_toggle); //get human/ai switch
        intent.putExtra("againstComputer", computerToggle.isChecked());

        Switch compulsoryToggle = (Switch) findViewById(R.id.compulsoryToggle); //get optional/forced capture switch
        intent.putExtra("optionalCapture", compulsoryToggle.isChecked());

        intent.putExtra("defaultColours", false); //assume both colours have been selected

        RadioButton player1White = (RadioButton) findViewById(R.id.player1White); //fetch radiobuttons
        RadioButton player1Red = (RadioButton) findViewById(R.id.player1Red);
        RadioButton player1Black = (RadioButton) findViewById(R.id.player1Black);
        if (player1White.isChecked()){ //decide which is chosen
            intent.putExtra("player1Colour", 0);
        } else if (player1Red.isChecked()){
            intent.putExtra("player1Colour", 1);
        } else if (player1Black.isChecked()){
            intent.putExtra("player1Colour", 2);
        } else {
            intent.putExtra("defaultColours", true); //if none chosen, set colours to default
        }

        RadioButton player2White = (RadioButton) findViewById(R.id.player2White); //fetch radiobuttons
        RadioButton player2Red = (RadioButton) findViewById(R.id.player2Red);
        RadioButton player2Black = (RadioButton) findViewById(R.id.player2Black);
        if (player2White.isChecked()){ //decide which is chosen
            intent.putExtra("player2Colour", 0);
        } else if (player2Red.isChecked()){
            intent.putExtra("player2Colour", 1);
        } else if (player2Black.isChecked()){
            intent.putExtra("player2Colour", 2);
        } else {
            intent.putExtra("defaultColours", true); //if none chosen, set colours to default
        }

        if (intent.getExtras().getInt("player1Colour") == intent.getExtras().getInt("player2Colour")){
            intent.putExtra("defaultColours", true); //if both chosen same, set colours to default
        }

        setResult(RESULT_OK, intent);
        super.finish();
    }


}
