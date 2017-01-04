package com.example.stuart.draughts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    public Game game;
    RelativeLayout layout;

    //details about screen dimensions
    int boardSize;
    int square0width;
    int square0height;
    int squareSize;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            String text = "You click at x = " + event.getX() + " and y = " + event.getY();
            System.out.println(text);

            if (game.isPlayer1Turn() || !(game.isAgainstComputer())) { //if it's a human's turn, the user needs to input
                int bit = touchToBit((int) event.getX(), (int) event.getY()); //get bit value of square tapped on
                Boolean turnFinished = game.userInput(bit); //pass on input to game to deal with
                displayGame();
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //find dimensions dynamically
        DisplayMetrics metrics = getResources().getDisplayMetrics(); //metrics holds information about the display
        boardSize = (int)(metrics.widthPixels - 50*metrics.density); //size is width of screen minus 50dp (dp is converted to px by multiplying by density)
        square0width = ((metrics.widthPixels - boardSize)/2); //first column from the left
        square0height = ((metrics.heightPixels + boardSize/2)/2); //first row up from bottom
        squareSize = (boardSize/8);

        //Set up relativelayout
        layout = new RelativeLayout(this);
        layout.setBackgroundColor(getResources().getColor(R.color.background));

        //Set up game board
        ImageView gameBoard = new ImageView(this);
        gameBoard.setId(R.id.gameBoard);
        gameBoard.setImageResource(R.drawable.boardv6);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                boardSize,
                boardSize);
        gameBoardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameBoardParams.addRule(RelativeLayout.CENTER_VERTICAL);

        //set up player 1 label
        TextView player1Label = new TextView(this);
        player1Label.setId(R.id.player1Label);
        player1Label.setText(R.string.player1);
        player1Label.setTextSize(30);
        RelativeLayout.LayoutParams player1LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player1LabelParams.addRule(RelativeLayout.BELOW, gameBoard.getId());
        player1LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //set up player 2 label
        TextView player2Label = new TextView(this);
        player2Label.setId(R.id.player2Label);
        player2Label.setText(R.string.player2);
        player2Label.setTextSize(30);
        RelativeLayout.LayoutParams player2LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player2LabelParams.addRule(RelativeLayout.ABOVE, gameBoard.getId());
        player2LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //add views into layout
        layout.addView(gameBoard, gameBoardParams);
        layout.addView(player1Label, player1LabelParams);
        layout.addView(player2Label, player2LabelParams);
        setContentView(layout);

        //coordinates of each square on the board, in pixels
        int[][] coordinates = new int[][]{
                {0,0}, {0,0}, {0,0}, {0,0}, {0,0},
                {square0width, square0height}, {square0width+2*squareSize, square0height},  {square0width+4*squareSize, square0height},  {square0width+6*squareSize, square0height}, {0,0},
                {square0width+squareSize, square0height-squareSize}, {square0width+3*squareSize, square0height-squareSize}, {square0width+5*squareSize, square0height-squareSize}, {square0width+7*squareSize, square0height-squareSize},
                {square0width, square0height-2*squareSize}, {square0width+2*squareSize, square0height-2*squareSize},  {square0width+4*squareSize, square0height-2*squareSize},  {square0width+6*squareSize, square0height-2*squareSize}, {0,0},
                {square0width+squareSize, square0height-3*squareSize}, {square0width+3*squareSize, square0height-3*squareSize}, {square0width+5*squareSize, square0height-3*squareSize}, {square0width+7*squareSize, square0height-3*squareSize},
                {square0width, square0height-4*squareSize}, {square0width+2*squareSize, square0height-4*squareSize},  {square0width+4*squareSize, square0height-4*squareSize},  {square0width+6*squareSize, square0height-4*squareSize}, {0,0},
                {square0width+squareSize, square0height-5*squareSize}, {square0width+3*squareSize, square0height-5*squareSize}, {square0width+5*squareSize, square0height-5*squareSize}, {square0width+7*squareSize, square0height-5*squareSize},
                {square0width, square0height-6*squareSize}, {square0width+2*squareSize, square0height-6*squareSize},  {square0width+4*squareSize, square0height-6*squareSize},  {square0width+6*squareSize, square0height-6*squareSize}, {0,0},
                {square0width+squareSize, square0height-7*squareSize}, {square0width+3*squareSize, square0height-7*squareSize}, {square0width+5*squareSize, square0height-7*squareSize}, {square0width+7*squareSize, square0height-7*squareSize},
                {0,0}, {0,0}, {0,0}, {0,0}, {0,0}
        };

        game = new Game(coordinates, getIntent().getBooleanExtra("AGAINST_COMPUTER", true), getIntent().getBooleanExtra("PLAYER_1_BLACK", true));
        displayGame();

        System.out.println("DONE");
    }

    //displays counters onto the screen
    private void displayGame(){
        //remove old counters from layout
        System.out.println("Length of counterViews:");
        int x = 0;
        for (ImageView counter : game.getCounterViews()){
            if (counter == null){
                break;
            }
            x++;
        }
        System.out.println(x);
        for (ImageView counter : game.getCounterViews()){
            if (counter == null){
                break;
            }
            ((ViewGroup) counter.getParent()).removeView(counter);
        }

        //add in new counters
        game.updateCounterViews(this);
        for (ImageView counter : game.getCounterViews()){
            if (counter == null){
                break;
            }
            layout.addView(counter);
        }
        setContentView(layout);
    }

    //takes touch coordinates and returns the closest square on the board, as a bitboard index
    private int touchToBit(int x, int y){
        //find square number in x and y
        int squareLeftEdge = square0width;
        int squareBottomEdge = square0height + 3*squareSize;
        int squareNumX = -1;
        int squareNumY = -1;

        while (x > squareLeftEdge){
            squareNumX ++;
            squareLeftEdge += squareSize;
        }
        while (y < squareBottomEdge){
            squareNumY ++;
            squareBottomEdge -= squareSize;
        }
        int[][] lookupArray = new int[][]{   //irritating 2d array converts xy coordinates into bit number
                {5, -1, 6, -1, 7, -1, 8, -1},
                {-1, 10, -1, 11, -1, 12, -1, 13},
                {14, -1, 15, -1, 16, -1, 17, -1},
                {-1, 19, -1, 20, -1, 21, -1, 22},
                {23, -1, 24, -1, 25, -1, 26, -1},
                {-1, 28, -1, 29, -1, 30, -1, 31},
                {32, -1, 33, -1, 34, -1, 35, -1},
                {-1, 37, -1, 38, -1, 39, -1, 40}
        };
        if (0 <= squareNumX && squareNumX <= 7 && 0 <= squareNumY && squareNumY <= 7){
            return lookupArray[squareNumY][squareNumX];
        } else {
            return -1;
        }


    }

}
