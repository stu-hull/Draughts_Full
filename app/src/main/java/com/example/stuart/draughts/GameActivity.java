package com.example.stuart.draughts;

import android.os.Handler;
import android.os.Message;
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

    Boolean inGame = true;

    ImageView[] counterViews = new ImageView[24];
    int highlighted;
    Boolean onlyMultiJump = false;
    static int[] counterIds = new int[]{ //array of every id to assign each counter
            R.id.counter0, R.id.counter1, R.id.counter2, R.id.counter3, R.id.counter4, R.id.counter5,
            R.id.counter6, R.id.counter7, R.id.counter8, R.id.counter9, R.id.counter10, R.id.counter11,
            R.id.counter12, R.id.counter13, R.id.counter14, R.id.counter15, R.id.counter16, R.id.counter17,
            R.id.counter18, R.id.counter19, R.id.counter20, R.id.counter21, R.id.counter22, R.id.counter23
    };

    //handler sends a toast message to the screen
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String text = bundle.getString("toastMessage");
            Boolean bool = bundle.getBoolean("updateRequest");
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
            if (bool){
                displayGame();
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

        //coordinates of each square on the board, in pixels
        coordinates = new int[][]{
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

        game = new Game(getIntent().getBooleanExtra("AGAINST_COMPUTER", true), getIntent().getBooleanExtra("PLAYER_1_BLACK", true)); //set up game with passed in extras

        updateCounterViews();
        for (ImageView counter : counterViews){ //add counter views into layout
            if (counter == null){
                break;
            }
            layout.addView(counter);
        }


        setContentView(layout); //display layout

        System.out.println("DONE");
    }

    //onTouchEvent manages screen touch and updates game as necessary, initialises new thread to run AI in
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (inGame && (game.isPlayer1Turn() || !(game.isAgainstComputer()))) { //if it's a human's turn, the user needs to input
                int bit = touchToBit((int) event.getX(), (int) event.getY()); //get bit value of square tapped on
                Board newBoard = userInput(bit); //pass on input to userInput to deal with
                if (newBoard != null){ //if board returned, change currentBoard
                    game.setCurrentBoard(newBoard, onlyMultiJump, highlighted);
                }
                displayGame(); //display new game
                System.out.println("Your move displayed");
                if (!game.isPlayer1Turn() && game.isAgainstComputer()){ //if computer's turn
                    Runnable runnable = new Runnable() { //setup new thread
                        public void run() {
                            Message msg = handler.obtainMessage(); //send toast to handler
                            Bundle bundle = new Bundle();
                            bundle.putString("toastMessage", getString(R.string.toast1));
                            bundle.putBoolean("updateRequest", false);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            game.setCurrentBoard(Ai.minimax(game.getCurrentBoard(), !game.isPlayer1Black(), 6), false, highlighted); //run minimax algorithm in new thread
                            msg = handler.obtainMessage(); //send toast to handler
                            bundle = new Bundle();
                            bundle.putString("toastMessage", getString(R.string.toast2));
                            bundle.putBoolean("updateRequest", true);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    };
                    Thread myThread = new Thread(runnable); //run thread
                    myThread.start();
                }
                if (game.getLegalMoves().length == 0){
                    inGame = false;
                    for (ImageView counter : counterViews){ //remove counters from screen
                        if (counter == null){
                            break;
                        }
                        ((ViewGroup) counter.getParent()).removeView(counter);
                    }
                    TextView gameOverMessage = new TextView(this);
                    gameOverMessage.setId(R.id.gameOverMessage);
                    if (game.isAgainstComputer() && game.isPlayer1Turn()){ //if player lost against computer
                        gameOverMessage.setText(R.string.youLost);
                        gameOverMessage.setTextColor(getResources().getColor(R.color.loseColour));
                    } else {
                        gameOverMessage.setTextColor(getResources().getColor(R.color.winColour));
                        if (game.isAgainstComputer() && !(game.isPlayer1Turn())){ //if player 1 won against computer
                            gameOverMessage.setText(R.string.youWon);
                        } else if (game.isPlayer1Turn()){ //if player 2 won against human
                            gameOverMessage.setText(R.string.player2Won);
                        } else { //else player 1 won
                            gameOverMessage.setText(R.string.player1Won);
                        }
                    }
                    gameOverMessage.setTextSize(50);
                    RelativeLayout.LayoutParams gameOverMessageParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    gameOverMessageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    gameOverMessageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    layout.addView(gameOverMessage, gameOverMessageParams);

                    setContentView(layout);
                }

            } else if (!inGame){ //else if the game has ended, return to main menu upon touch
                finish();
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
    private Board userInput(int bit){
        System.out.println(game.getLegalMoves().length);
        if (game.isAgainstComputer() && !(game.isPlayer1Turn())){
            throw new IllegalStateException("userInput called when no user input is required");
        }
        if (highlighted == -1){ //if nothing highlighted
            System.out.println("1 Nothing already highlighted");
            if (!(game.getCurrentBoard().isEmpty(bit)) && (game.getCurrentBoard().isBlack(bit) == (game.isPlayer1Black() == game.isPlayer1Turn()))){ //if tapped on counter and counter is right colour, highlight counter
                highlighted = bit;
                System.out.println("1.1 Counter highlighted");
            }
        } else {
            System.out.println("2 Something already highlighted");
            if (game.getCurrentBoard().isEmpty(bit)) { //if tapped on empty space
                System.out.println("2.1 Tapped on clear space");
                Board newBoard = game.getCurrentBoard().makeSimpleMove(highlighted, bit); //make move on new board
                System.out.println(game.isPlayer1Turn());
                Boolean legal = false;
                for (Board move : game.getLegalMoves()) { //test if legal
                    /*System.out.println(Long.toBinaryString(newBoard.getBlackPieces()));
                    System.out.println(Long.toBinaryString(newBoard.getKings()));
                    System.out.println(Long.toBinaryString(newBoard.getWhitePieces()));
                    System.out.println(Long.toBinaryString(move.getBlackPieces()));
                    System.out.println(Long.toBinaryString(move.getKings()));
                    System.out.println(Long.toBinaryString(move.getWhitePieces()));*/
                    if (move.getBlackPieces() == newBoard.getBlackPieces() && move.getWhitePieces() == newBoard.getWhitePieces() && move.getKings() == newBoard.getKings()) {
                        legal = true;
                        break;
                    }
                }
                if (legal) {
                    System.out.println("2.1.1 Move requested is legal");
                    if ((highlighted - bit) * (highlighted - bit) <= 25) { //if a slide was made rather than a jump, make move and reset (unless in multijump, in which case do nothing)
                        System.out.println("2.1.1.1 Move requested is a slide");
                        if (!(onlyMultiJump)) {
                            System.out.println("2.1.1.1.1 Not in multijump- confirm move, reset, unhighlight");
                            //currentBoard = newBoard;
                            highlighted = -1;
                            //player1Turn = !player1Turn;
                            //legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                            return newBoard;
                        }
                    } else {
                        System.out.println("2.1.1.2 Move requested is a jump");
                        Board[] newNewBoards = newBoard.findMultiJump(bit); //find all further possible jumps
                        if (newNewBoards.length > 1) { //if more jumps available, make move, keep piece highlighted, and record that only multi jumps allowed
                            System.out.println("2.1.1.2.1 Move has further jump options- onlymultijump enabled, piece moved and highlighted");
                            //currentBoard = newBoard;
                            //legalMoves = currentBoard.findMultiJump(bit); //only legal moves are the multi jump ones
                            highlighted = bit;
                            onlyMultiJump = true;
                            return newBoard;
                        } else { //if no further jumps, make move, change player to nextPlayer and reset
                            System.out.println("2.1.1.2.2 Move has no further jumps- confirm move, reset and unhighlight");
                            //currentBoard = newBoard;
                            highlighted = -1;
                            //player1Turn = !player1Turn;
                            onlyMultiJump = false;
                            //legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                            return newBoard;
                        }
                    }
                } else {
                    System.out.println("2.1.2 Move requested is illegal");
                    if (!onlyMultiJump) {
                        System.out.println("2.1.2.1 Not in multijump- unhighlight");
                        highlighted = -1; //if illegal, reset (ignore if in multijump)
                    }
                }
            } else if (onlyMultiJump){
                System.out.println("2.2 Tapped on counter but in multijump");
                if (bit == highlighted) { //if doing a multijump and the player taps the piece, stop the jump and end the turn (if they tap another piece, ignore
                    System.out.println("2.2.1 Tapped on highlighted counter, stop jump");
                    highlighted = -1;
                    //player1Turn = !player1Turn;
                    onlyMultiJump = false;
                    //legalMoves = currentBoard.findMoves(isPlayer1Black() == player1Turn); //current colour to move is dependent on whether player 1 is black and whose turn it is
                    return game.getCurrentBoard();
                }
            } else {
                System.out.println("2.3 Tapped on counter, unhighlight");
                highlighted = -1; //else reset
            }
        }
        return null;
    }

    //creates ImageViews for counter positions using Game's currentBoard and screen dimensions
    private void updateCounterViews(){

        int counterIndex = 0;
        int drawableId;
        counterViews = new ImageView[24];

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

            //create counter, assign ID & drawable
            ImageView counter = new ImageView(this);
            counter.setId(counterIds[counterIndex]);
            counter.setImageResource(drawableId);

            RelativeLayout.LayoutParams counterParams = new RelativeLayout.LayoutParams(
                    squareSize,
                    squareSize
            );

            counterParams.leftMargin = coordinates[positionIndex][0]; //margins are fetched from coordinates array
            counterParams.topMargin = coordinates[positionIndex][1];

            counter.setLayoutParams(counterParams);
            counterViews[counterIndex] = counter;
            counterIndex++;

        }

    }

    //displays counters in counterViews onto the screen
    private void displayGame(){
        //remove old counters from layout
        for (ImageView counter : counterViews){
            if (counter == null){
                break;
            }
            ((ViewGroup) counter.getParent()).removeView(counter);
        }

        //add in new counters
        updateCounterViews();
        for (ImageView counter : counterViews){
            if (counter == null){
                break;
            }
            layout.addView(counter);
        }
        setContentView(layout);
    }

}
