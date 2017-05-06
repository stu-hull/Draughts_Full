package com.Draughts2.stuart.draughts;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;

public class Settings extends AppCompatActivity {

    TextView difficultyLabel;
    RadioGroup difficultyGroup;
    Boolean checked;

    LinearLayout layout;

    Spinner player1Spinner;
    Spinner player2Spinner;
    ImageView player1image;
    ImageView player2image;
    TextView warningMessage;

    Button startGame;
    Boolean goodToGo;

    Handler sleepCatch = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (checked){
                difficultyLabel.setVisibility(View.VISIBLE);
                difficultyGroup.setVisibility(View.VISIBLE);
            } else {
                difficultyLabel.setVisibility(View.GONE);
                difficultyGroup.setVisibility((View.GONE));
            }
            setContentView(layout);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        layout = (LinearLayout) findViewById(R.id.activity_settings);
        difficultyLabel = (TextView) findViewById(R.id.difficultyLabel);
        difficultyGroup = (RadioGroup) findViewById(R.id.difficultyGroup);

        player1Spinner = (Spinner) findViewById(R.id.player1ColourSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        player1Spinner.setAdapter(adapter);
        player1Spinner.setSelection(adapter.getPosition("Red"));
        player1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (player1Spinner.getSelectedItem().toString()){
                    case "White": player1image.setImageResource(R.drawable.whiteman); break;
                    case "Red": player1image.setImageResource(R.drawable.redman); break;
                    case "Black": player1image.setImageResource(R.drawable.blackman); break;
                }
                checkIfSpinnersSame();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                checkIfSpinnersSame();
            }

        });

        player2Spinner = (Spinner) findViewById(R.id.player2ColourSpinner);
        player2Spinner.setAdapter(adapter);
        player2Spinner.setSelection(adapter.getPosition("White"));
        player2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (player2Spinner.getSelectedItem().toString()){
                    case "White": player2image.setImageResource(R.drawable.whiteman); break;
                    case "Red": player2image.setImageResource(R.drawable.redman); break;
                    case "Black": player2image.setImageResource(R.drawable.blackman); break;
                }
                checkIfSpinnersSame();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                checkIfSpinnersSame();
            }

        });

        player1image = (ImageView) findViewById(R.id.player_1_counter_image);
        player2image = (ImageView) findViewById(R.id.player_2_counter_image);



        Switch computerOn = (Switch) findViewById(R.id.computer_toggle);
        computerOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                sleepCatch.sendEmptyMessageDelayed(1, 200L);
            }
        });

        warningMessage = (TextView) findViewById(R.id.colour_same_error);
        startGame = (Button) findViewById(R.id.new_game);
        goodToGo = true;
    }

    //checks if player has chosen 2 same colours
    private void checkIfSpinnersSame(){
        if (player1Spinner.getSelectedItem().toString().equals(player2Spinner.getSelectedItem().toString())){
            //colours same, don't let game start
            warningMessage.setVisibility(View.VISIBLE);
            startGame.setAlpha(.5f);
            startGame.setClickable(false);
        } else {
            //colours OK
            warningMessage.setVisibility(View.INVISIBLE);
            startGame.setAlpha(1f);
            startGame.setClickable(true);
        }
    }

    //Runs when "Start Game" button on home screen is pressed- starts game activity, passes settings to activity
    public void startGame(View view) {
        Intent intent = new Intent(Settings.this, GameActivity.class);

        Switch computerToggle = (Switch) findViewById(R.id.computer_toggle); //get human/ai switch
        intent.putExtra("againstComputer", computerToggle.isChecked());

        Switch compulsoryToggle = (Switch) findViewById(R.id.compulsoryToggle); //get optional/forced capture switch
        intent.putExtra("optionalCapture", compulsoryToggle.isChecked());

        intent.putExtra("player1Colour", player1Spinner.getSelectedItemPosition());
        intent.putExtra("player2Colour", player2Spinner.getSelectedItemPosition());

        startActivity(intent);
    }

}
