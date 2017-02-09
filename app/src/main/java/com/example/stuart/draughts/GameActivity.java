package com.example.stuart.draughts;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    TextView player1Label;
    TextView player2Label;
    Button undoButton;
    ProgressBar bar;

    int player1ManID;
    int player1KingID;
    int player2ManID;
    int player2KingID;

    int highlighted = -1;
    Boolean inGame = true;

    //handler sends a toast message to the screen
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int progress = (int) msg.getData().getFloat("progress");
            if (progress == 0){
                layout.addView(bar);
            }
            bar.setProgress(progress);
            if (progress >= 99){
                ((ViewGroup) bar.getParent()).removeView(bar);
                removeViews();
                addCounterViews(); //add in counters
                player1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yourTurn));
                player1Label.setTextSize(50);
                player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.notYourTurn));
                player2Label.setTextSize(30);
            }
            setContentView(layout);
        }
    };

    View.OnClickListener undoMove = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (game.getPreviousGameState() != null && !game.inMultiJump && inGame){ //if there's a previous gamestate, ie not just undoed and not right at the start, and not in middle of multijump
                game = game.getPreviousGameState();
                highlighted = -1;
                removeViews();
                addCounterViews();
                if (game.isPlayer1Turn()) { //set message of turnLabel and move it from top to bottom of screen
                    player1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yourTurn));
                    player1Label.setTextSize(50);
                    player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.notYourTurn));
                    player2Label.setTextSize(30);
                } else {
                    player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yourTurn));
                    player2Label.setTextSize(50);
                    player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.notYourTurn));
                    player1Label.setTextSize(30);
                }
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
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.background));

        //Set up game board
        ImageView gameBoard = new ImageView(this);
        gameBoard.setId(R.id.gameBoard);
        gameBoard.setImageResource(R.drawable.boardv7);
        RelativeLayout.LayoutParams gameBoardParams = new RelativeLayout.LayoutParams(
                boardSize,
                boardSize);
        gameBoardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameBoardParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gameBoard.setLayoutParams(gameBoardParams);
        layout.addView(gameBoard);

        //set up player 1 label
        player1Label = new TextView(this);
        player1Label.setId(R.id.player1Label);
        player1Label.setText(R.string.player1);
        player1Label.setTextSize(50);
        player1Label.setTextColor(ContextCompat.getColor(this, R.color.yourTurn));
        RelativeLayout.LayoutParams player1LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player1LabelParams.addRule(RelativeLayout.BELOW, gameBoard.getId());
        player1LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        player1Label.setLayoutParams(player1LabelParams);
        layout.addView(player1Label);

        //set up player 2 label
        player2Label = new TextView(this);
        player2Label.setId(R.id.player2Label);
        player2Label.setText(R.string.player2);
        player2Label.setTextSize(30);
        player2Label.setTextColor(ContextCompat.getColor(this, R.color.notYourTurn));
        RelativeLayout.LayoutParams player2LabelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        player2LabelParams.addRule(RelativeLayout.ABOVE, gameBoard.getId());
        player2LabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        player2Label.setLayoutParams(player2LabelParams);
        layout.addView(player2Label);

        //set up undo button
        undoButton = new Button(this);
        undoButton.setText(R.string.undoButton);
        undoButton.setTextSize(20);
        undoButton.setOnClickListener(undoMove);
        RelativeLayout.LayoutParams undoButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        undoButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        undoButtonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        undoButton.setLayoutParams(undoButtonParams);
        layout.addView(undoButton);

        //set up game over message
        gameOverMessage = new TextView(this);
        gameOverMessage.setTextSize(50);
        RelativeLayout.LayoutParams gameOverMessageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        gameOverMessageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gameOverMessageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gameOverMessage.setLayoutParams(gameOverMessageParams);

        //set up AI progress bar
        bar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        bar.setId(R.id.progressBar);
        bar.getProgressDrawable().setColorFilter(0xFF000000 + R.color.progressBarColour, android.graphics.PorterDuff.Mode.MULTIPLY);
        //bar.setBackgroundColor(ContextCompat.getColor(this, R.color.progressBarColour));
        RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(
                300,
                30);
        barParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        barParams.addRule(RelativeLayout.ABOVE, R.id.player2Label);
        bar.setScaleY(3f);
        bar.setLayoutParams(barParams);

        //set up game with passed in extras
        Boolean againstComputer = getIntent().getBooleanExtra("againstComputer", false);
        Boolean optionalCapture = getIntent().getBooleanExtra("optionalCapture", false);
        Boolean player1Black = getIntent().getBooleanExtra("PLAYER_1_BLACK", true);
        game = new Game(againstComputer, player1Black, optionalCapture);

        //set up counter images
        int player1Colour = getIntent().getIntExtra("player1Colour", 1); //get player 1 colour code
        switch (player1Colour){
            case 0:
                player1ManID = R.drawable.whiteman; //choose image based on colour code
                player1KingID = R.drawable.whiteking;
                break;
            case 1:
                player1ManID = R.drawable.redman;
                player1KingID = R.drawable.redking;
                break;
            case 2:
                player1ManID = R.drawable.blackman;
                player1KingID = R.drawable.blackking;
                break;
            default:
                player1ManID = R.drawable.redman;
                player1KingID = R.drawable.redking;
        }

        int player2Colour = getIntent().getIntExtra("player2Colour", 0); //get player 1 colour code
        switch (player2Colour){
            case 0:
                player2ManID = R.drawable.whiteman; //choose image based on colour code
                player2KingID = R.drawable.whiteking;
                break;
            case 1:
                player2ManID = R.drawable.redman;
                player2KingID = R.drawable.redking;
                break;
            case 2:
                player2ManID = R.drawable.blackman;
                player2KingID = R.drawable.blackking;
                break;
            default:
                player2ManID = R.drawable.whiteman;
                player2KingID = R.drawable.whiteking;
        }

        addCounterViews(); //add counters to layout

        setContentView(layout); //display layout
        System.out.println("DONE");
    }

    //onTouchEvent manages screen touch and updates game as necessary, initialises new thread to run AI in
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!inGame) { //if the game has ended, return to main menu upon touch
                finish();

            } else if (game.isPlayer1Turn() || !game.isAgainstComputer()) { //else if it's a human's turn, the user needs to input
                int bit = touchToBit((int) event.getX(), (int) event.getY()); //get bit value of square tapped on
                Board newBoard = userInput(bit); //pass on input to userInput to deal with

                if (newBoard != null) { //if board returned, change currentBoard
                    game.setCurrentBoard(newBoard, highlighted);
                }

                removeViews(); //clear views

                if (game.getLegalMoves().length == 0) { //if no moves left, game has ended
                    inGame = false;
                    //set up gameOverMessage depending on who won
                    gameOverMessage.setId(R.id.gameOverMessage);
                    if (game.isAgainstComputer() && game.isPlayer1Turn()) { //if player lost against computer
                        gameOverMessage.setText(R.string.youLost);
                        gameOverMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.loseColour));
                    } else {
                        gameOverMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.winColour));
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
                        player1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yourTurn));
                        player1Label.setTextSize(50);
                        player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.notYourTurn));
                        player2Label.setTextSize(30);
                    } else {
                        player2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.yourTurn));
                        player2Label.setTextSize(50);
                        player1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.notYourTurn));
                        player1Label.setTextSize(30);
                    }
                    setContentView(layout);

                    //if computer's turn, run AI in new thread
                    if (!game.isPlayer1Turn() && game.isAgainstComputer()) {
                        Runnable runnable = new Runnable() { //setup new thread
                            public void run() {
                                game.setCurrentBoard(minimax(game.getCurrentBoard(), !game.isPlayer1Black(), 9, game.isOptionalCapture()), highlighted); //run minimax algorithm in new thread
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

    //figures out what to display to the user from their touch location. The function also saves the current game state and returns a Board if the board is to be updated.
    @Nullable
    private Board userInput(int bit){
        if (game.isAgainstComputer() && !(game.isPlayer1Turn())){
            throw new IllegalStateException("userInput called when no user input is required");
        }
        if (highlighted == -1){ //if nothing highlighted
            if (!(game.getCurrentBoard().isEmpty(bit)) && (game.getCurrentBoard().isPlayer1(bit) == (game.isPlayer1Black() == game.isPlayer1Turn()))){ //if tapped on counter and counter is right colour, highlight counter
                highlighted = bit;
            }
        } else {
            if (game.getCurrentBoard().isEmpty(bit)) { //if tapped on empty space
                Board newBoard = game.getCurrentBoard().makeSimpleMove(highlighted, bit); //make move on new board
                Boolean legal = false;
                for (Board move : game.getLegalMoves()) { //test if legal
                    if (move.getBlackPieces() == newBoard.getBlackPieces() && move.getWhitePieces() == newBoard.getWhitePieces() && move.getKings() == newBoard.getKings()) {
                        legal = true;
                        break;
                    }
                }
                if (legal) {
                    if ((highlighted - bit) * (highlighted - bit) <= 25) { //if a slide was made rather than a jump, make move and reset (unless in multijump, in which case do nothing)
                        if (!(game.inMultiJump)) {
                            highlighted = -1;
                            game.saveGame();
                            return newBoard;
                        }
                    } else {
                        Board[] newNewBoards = newBoard.findMultiJump(bit); //find all further possible jumps
                        if (newNewBoards.length > 1) { //if more jumps available, make move, keep piece highlighted, and record that only multi jumps allowed
                            highlighted = bit;
                            game.inMultiJump = true;
                            return newBoard;
                        } else { //if no further jumps, make move, change player to nextPlayer and reset
                            highlighted = -1;
                            game.inMultiJump = false;
                            game.saveGame();
                            return newBoard;
                        }
                    }
                } else {
                    if (!game.inMultiJump) {
                        highlighted = -1; //if illegal, reset (ignore if in multijump)
                    }
                }
            } else if (game.inMultiJump){
                if (bit == highlighted) { //if doing a multijump and the player taps the piece, stop the jump and end the turn (if they tap another piece, ignore
                    highlighted = -1;
                    Board toReturn = game.getCurrentBoard();
                    game.inMultiJump = false;
                    game.saveGame();
                    return toReturn;
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

        for (int positionIndex = 0; positionIndex <= 40; positionIndex++) { //for each position
            //decide on image for counter
            int drawableID;
            if (positionIndex == highlighted && (game.getCurrentBoard().isPlayer1(positionIndex) || game.getCurrentBoard().isPlayer2(positionIndex))){
                if (game.getCurrentBoard().isKing(positionIndex)) {
                    drawableID = R.drawable.highlightedking;
                } else {
                    drawableID = R.drawable.highlightedman;
                }
            } else if (game.getCurrentBoard().isPlayer1(positionIndex)){
                if (game.getCurrentBoard().isKing(positionIndex)){
                    drawableID = player1KingID;
                } else {
                    drawableID = player1ManID;
                }
            } else if (game.getCurrentBoard().isPlayer2(positionIndex)) {
                if (game.getCurrentBoard().isKing(positionIndex)){
                    drawableID = player2KingID;
                } else {
                    drawableID = player2ManID;
                }
            } else {
                continue; //if not black or white, continue loop
            }

            //setup counter using ID & drawable
            ImageView counter = new ImageView(this);
            counter.setId(counterIds[counterIndex]);
            counter.setImageResource(drawableID);
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

    //removes all views from layout, excluding gameboard, player labels
    private void removeViews(){
        //remove counters from layout
        for (int id : counterIds){
            ImageView counter = (ImageView) findViewById(id);
            if (counter == null){
                break;
            }
            ((ViewGroup) counter.getParent()).removeView(counter);
        }
    }

    //This is the algorithm called by the AI, it actually runs the real algorithm on each move available and then chooses the best (the real minimax returns a value, not the best move)
    private Board minimax(Board currentBoard, boolean isBlack, int depth, boolean optionalCapture){

        //sleep thread for a second to allow user to see moves more easily
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { //if interrupted, not really important, just carry on
        }

        Board[] availableMoves = currentBoard.findMoves(isBlack, optionalCapture); //find all the moves available to the player

        Board bestMove = new Board(); //the best possible move
        bestMove.nullBoard(); //if no moves are available, this empty board will be returned and the Game will recognise this as a surrender
        double bestScore; //the value of the best possible move
        double currentScore; //the value of the current move

        if (isBlack) { //if player is black, high scores are good, so default score is as low as possible
            bestScore = Double.NEGATIVE_INFINITY;
        } else { //if player white, low scores are good, so default score is as high as possible
            bestScore = Double.POSITIVE_INFINITY;
        }
        int count = 0;
        for (Board currentMove : availableMoves) { //for each move in the list of possible moves
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            float progress =  count*100/availableMoves.length;
            bundle.putFloat("progress", progress); //tell handler done
            msg.setData(bundle);
            handler.sendMessage(msg); //send progress update to handler

            currentScore = Ai.minimaxV2(currentMove, isBlack, depth-1, optionalCapture, -Double.MAX_VALUE, Double.MAX_VALUE); //score of current move is found with minimax
            if ((isBlack && currentScore > bestScore) || (!isBlack && currentScore < bestScore)){ //if current move is better than all before it (or worse if you're white)
                bestScore = currentScore; //set best score to score of current move
                bestMove = currentMove; //record the current move to be returned (this is the bit the other algorithm won't do)
            }
            count++;
        }
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        float progress =  count*100/availableMoves.length;
        bundle.putFloat("progress", progress); //tell handler done
        msg.setData(bundle);
        handler.sendMessage(msg);

        return bestMove;
    } //DONE //TESTED

}
