package com.example.stuart.draughts;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
    int[][] coordinates;

    //Views for different parts of the layout
    static int[] counterIds = new int[]{ //array of every id to assign each counter
            R.id.counter0, R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5,
            R.id.counter6, R.id.counter7, R.id.counter8, R.id.counter9, R.id.counter10, R.id.counter11,
            R.id.counter12, R.id.counter13, R.id.counter14, R.id.counter15, R.id.counter16, R.id.counter17,
            R.id.counter18, R.id.counter19, R.id.counter20, R.id.counter21, R.id.counter22, R.id.counter23
    };
    TextView gameOverMessage;
    TextView turnLabel;

    int highlighted;
    Boolean inGame = true;
    Boolean onlyMultiJump = false;

    //handler sends a toast message to the screen
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String text = bundle.getString("toastMessage");
            Boolean endTurn = bundle.getBoolean("endTurn");
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
            if (endTurn){
                removeViews();
                addCounterViews(); //add in counters
                turnLabel.setText(R.string.player1Move); //update turnLabel
                RelativeLayout.LayoutParams turnLabelParams = (RelativeLayout.LayoutParams) turnLabel.getLayoutParams();
                turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                turnLabel.setLayoutParams(turnLabelParams);
                layout.addView(turnLabel);
                setContentView(layout);
            }
        }
    };

    //sets up display, game, etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //find dimensions dynamically
        DisplayMetrics metrics = getResources().getDisplayMetrics(); //metrics holds information about the display
        boardSize = (int)(metrics.widthPixels - 50*metrics.density); //size is width of screen minus 50dp (dp is converted to px by multiplying by density)
        squareSize = (boardSize/8);
        square0width = ((metrics.widthPixels - boardSize)/2); //first column from the left
        square0height = (int) (((0.82*metrics.heightPixels + boardSize)/2));// - 1.35*squareSize); //first row up from bottom
        coordinates = new int[][]{ //coordinates of each square on the board, in pixels
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
        gameBoard.setLayoutParams(gameBoardParams);
        layout.addView(gameBoard);

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
        player1Label.setLayoutParams(player1LabelParams);
        layout.addView(player1Label);

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
        player2Label.setLayoutParams(player2LabelParams);
        layout.addView(player2Label);

        //set up turn label which indicates whose turn it is
        turnLabel = new TextView(this);
        turnLabel.setTextSize(30);
        turnLabel.setText(R.string.player1Move);
        RelativeLayout.LayoutParams turnLabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        turnLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        turnLabel.setLayoutParams(turnLabelParams);
        layout.addView(turnLabel);

        //set up game over message
        gameOverMessage = new TextView(this);
        gameOverMessage.setTextSize(50);
        RelativeLayout.LayoutParams gameOverMessageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        gameOverMessageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameOverMessageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gameOverMessage.setLayoutParams(gameOverMessageParams);

        //set up game with passed in extras
        game = new Game(getIntent().getBooleanExtra("AGAINST_COMPUTER", true), getIntent().getBooleanExtra("PLAYER_1_BLACK", true));

        addCounterViews();

        setContentView(layout); //display layout
        System.out.println("DONE");
    }

    //onTouchEvent manages screen touch and updates game as necessary, initialises new thread to run AI in
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!inGame) { //else if the game has ended, return to main menu upon touch
                finish();

            } else if (game.isPlayer1Turn() || !game.isAgainstComputer()) { //if it's a human's turn, the user needs to input

                System.out.println("Point 1");

                int bit = touchToBit((int) event.getX(), (int) event.getY()); //get bit value of square tapped on
                Board newBoard = userInput(bit); //pass on input to userInput to deal with

                if (newBoard != null) { //if board returned, change currentBoard
                    game.setCurrentBoard(newBoard, onlyMultiJump, highlighted);
                }

                removeViews(); //clear views

                if (game.getLegalMoves().length == 0) { //if no moves left, game has ended
                    inGame = false;
                    //set up gameOverMessage depending on who won
                    gameOverMessage.setId(R.id.gameOverMessage);
                    if (game.isAgainstComputer() && game.isPlayer1Turn()) { //if player lost against computer
                        gameOverMessage.setText(R.string.youLost);
                        gameOverMessage.setTextColor(getResources().getColor(R.color.loseColour));
                    } else {
                        gameOverMessage.setTextColor(getResources().getColor(R.color.winColour));
                        if (game.isAgainstComputer() && !(game.isPlayer1Turn())) { //if player 1 won against computer
                            gameOverMessage.setText(R.string.youWon);
                        } else if (game.isPlayer1Turn()) { //if player 2 won against human
                            gameOverMessage.setText(R.string.player2Won);
                        } else { //else player 1 won
                            gameOverMessage.setText(R.string.player1Won);
                        }
                    }
                    layout.addView(gameOverMessage);

                    setContentView(layout); //GameOverMessage is only view that needs to be shown

                } else { //else game still in play

                    addCounterViews(); //add in counters
                    if (game.isPlayer1Turn()) { //set message of turnLabel and move it from top to bottom of screen
                        turnLabel.setText(R.string.player1Move);
                        RelativeLayout.LayoutParams turnLabelParams = (RelativeLayout.LayoutParams) turnLabel.getLayoutParams();
                        turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        turnLabel.setLayoutParams(turnLabelParams);
                    } else {
                        turnLabel.setText(R.string.player2Move);
                        RelativeLayout.LayoutParams turnLabelParams = (RelativeLayout.LayoutParams) turnLabel.getLayoutParams();
                        turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        turnLabelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        turnLabel.setLayoutParams(turnLabelParams);
                    }
                    layout.addView(turnLabel);
                    setContentView(layout);

                    //if computer's turn, run AI in new thread
                    if (!game.isPlayer1Turn() && game.isAgainstComputer()) {
                        Runnable runnable = new Runnable() { //setup new thread
                            public void run() {
                                Message msg = handler.obtainMessage(); //send toast to handler
                                Bundle bundle = new Bundle();
                                bundle.putString("toastMessage", getString(R.string.toast1));
                                bundle.putBoolean("endTurn", false);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                                game.setCurrentBoard(Ai.minimax(game.getCurrentBoard(), !game.isPlayer1Black(), 7), false, highlighted); //run minimax algorithm in new thread
                                msg = handler.obtainMessage(); //send toast to handler
                                bundle = new Bundle();
                                bundle.putString("toastMessage", getString(R.string.toast2));
                                bundle.putBoolean("endTurn", true);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        };
                        Thread myThread = new Thread(runnable); //run thread
                        myThread.start();
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    //takes touch coordinates and returns the closest square on the board, as a bitboard index
    private int touchToBit(int x, int y){
        //find square number in x and y
        int squareLeftEdge = square0width;
        int squareBottomEdge = (int) (square0height + 1.6*squareSize);
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

    //figures out what to display to the user from their touch location. The function also returns a Board if the board is to be updated.
    @Nullable
    private Board userInput(int bit){
        System.out.println(game.getLegalMoves().length);
        if (game.isAgainstComputer() && !(game.isPlayer1Turn())){
            throw new IllegalStateException("userInput called when no user input is required");
        }
        if (highlighted == -1){ //if nothing highlighted
            if (!(game.getCurrentBoard().isEmpty(bit)) && (game.getCurrentBoard().isBlack(bit) == (game.isPlayer1Black() == game.isPlayer1Turn()))){ //if tapped on counter and counter is right colour, highlight counter
                highlighted = bit;
            }
        } else {
            if (game.getCurrentBoard().isEmpty(bit)) { //if tapped on empty space
                Board newBoard = game.getCurrentBoard().makeSimpleMove(highlighted, bit); //make move on new board
                System.out.println(game.isPlayer1Turn());
                Boolean legal = false;
                for (Board move : game.getLegalMoves()) { //test if legal
                    if (move.getBlackPieces() == newBoard.getBlackPieces() && move.getWhitePieces() == newBoard.getWhitePieces() && move.getKings() == newBoard.getKings()) {
                        legal = true;
                        break;
                    }
                }
                if (legal) {
                    if ((highlighted - bit) * (highlighted - bit) <= 25) { //if a slide was made rather than a jump, make move and reset (unless in multijump, in which case do nothing)
                        if (!(onlyMultiJump)) {
                            highlighted = -1;
                            return newBoard;
                        }
                    } else {
                        Board[] newNewBoards = newBoard.findMultiJump(bit); //find all further possible jumps
                        if (newNewBoards.length > 1) { //if more jumps available, make move, keep piece highlighted, and record that only multi jumps allowed
                            highlighted = bit;
                            onlyMultiJump = true;
                            return newBoard;
                        } else { //if no further jumps, make move, change player to nextPlayer and reset
                            highlighted = -1;
                            onlyMultiJump = false;
                            return newBoard;
                        }
                    }
                } else {
                    if (!onlyMultiJump) {
                        highlighted = -1; //if illegal, reset (ignore if in multijump)
                    }
                }
            } else if (onlyMultiJump){
                if (bit == highlighted) { //if doing a multijump and the player taps the piece, stop the jump and end the turn (if they tap another piece, ignore
                    highlighted = -1;
                    onlyMultiJump = false;
                    return game.getCurrentBoard();
                }
            } else {
                highlighted = -1; //else reset
            }
        }
        return null;
    }

    //adds ImageViews into layout for counter positions using Game's currentBoard and screen dimensions
    private void addCounterViews(){

        int counterIndex = 0;
        int drawableId;

        for (int positionIndex = 0; positionIndex <= 40; positionIndex++) { //for each position
            //decide on image for counter
            if (positionIndex == highlighted && (game.getCurrentBoard().isBlack(positionIndex) || game.getCurrentBoard().isWhite(positionIndex))){
                if (game.getCurrentBoard().isKing(positionIndex)) {
                    drawableId = R.drawable.kinghighlighted;
                } else {
                    drawableId = R.drawable.manhighlighted;
                }
            } else if (game.getCurrentBoard().isBlack(positionIndex)){
                if (game.getCurrentBoard().isKing(positionIndex)){
                    drawableId = R.drawable.redking;
                } else {
                    drawableId = R.drawable.redman;
                }
            } else if (game.getCurrentBoard().isWhite(positionIndex)) {
                if (game.getCurrentBoard().isKing(positionIndex)){
                    drawableId = R.drawable.whiteking;
                } else {
                    drawableId = R.drawable.whiteman;
                }
            } else {
                continue; //if not black or white, continue loop
            }

            //setup counter using ID & drawable
            ImageView counter = new ImageView(this);
            counter.setId(counterIds[counterIndex]);
            counter.setImageResource(drawableId);
            RelativeLayout.LayoutParams counterParams = new RelativeLayout.LayoutParams(
                    squareSize,
                    squareSize);
            counterParams.leftMargin = coordinates[positionIndex][0]; //margins are fetched from coordinates array
            counterParams.topMargin = coordinates[positionIndex][1];
            counter.setLayoutParams(counterParams);

            layout.addView(counter);
            counterIndex++;

        }

    }

    //removes all views from layout, excluding gameboard and player labels
    private void removeViews(){
        //remove counters from layout
        for (int id : counterIds){
            ImageView counter = (ImageView) findViewById(id);
            if (counter == null){
                break;
            }
            ((ViewGroup) counter.getParent()).removeView(counter);
        }

        //remove turnLabel from layout
        try{
            ((ViewGroup) turnLabel.getParent()).removeView(turnLabel);
        } catch (java.lang.NullPointerException e){} //except if not already in layout

        //remove gameOverMessage from layout
        try{
            ((ViewGroup) gameOverMessage.getParent()).removeView(gameOverMessage);
        } catch (java.lang.NullPointerException e){} //except if not already in layout
    }

}
