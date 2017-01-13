package com.example.stuart.draughts;

/**
 * Created by Stuart on 30/08/2016.
 * A board representation, holding a board state using bitboards and small functions to query the board
 * NOTE: this board representation is limited to the rules; it cannot be freely edited and moves must be in accordance with the rules
 */
class Board {

    /*
    board layout:
      37  38  39  40
    32  33  34  35
      28  29  30  31
    23  24  25  26
      19  20  21  22
    14  15  16  17
      10  11  12  13
    05  06  07  08
    */

    //useful masks for different types of squares on the board
    private static long maskValid = 0b0000011110111111110111111110111111110111100000L; //all the valid board squares (the 0s are padding)
    static long maskBlackBack = 0b0000011110000000000000000000000000000000000000L; //the home row squares for black
    static long maskWhiteBack = 0b0000000000000000000000000000000000000111100000L; //the home row squares for white
    static long maskCenter = 0b0000000000000000000011001100000000000000000000L; //the central 8 squares (4 playable squares)

    //takes an integer board position, and creates a mask for that position
    private static long findMask(int position){
        return 1L<<(long)(45-position);
    } //DONE //TESTED

    //takes an integer board position and finds whether that board position is valid
    private static Boolean isValid(int position){
        return ((maskValid & 1L<<(long)(45-position)) != 0L);
    }

    //trims the excess off of an array of Boards
    private static Board[] trim(Board[] array){
        int length = 0;
        while (array[length] != null){
            length += 1;
        }
        Board[] newArray = new Board[length];
        System.arraycopy(array, 0, newArray, 0, length);
        return newArray;
    }  //...DONE //TESTED

    //the 3 base bitboards used to permanently represent the board (cannot be freely edited)
    private long blackPieces; //stores the location of all black pieces
    private long whitePieces; //stores the location of all white pieces
    private long kings; //stores the location of every king, black or white

    long getBlackPieces(){
        return blackPieces;
    } //getters for each bitboard
    long getWhitePieces() {
        return whitePieces;
    }
    long getKings() {
        return kings;
    }

    //constructors / methods to set up the board
    Board(){
        blackPieces = 0b0000011110111111110000000000000000000000000000L;
        whitePieces = 0b0000000000000000000000000000111111110111100000L;
        kings =       0b0000000000000000000000000000000000000000000000L;
    } //default constructor sets up a board in the starting position
    void nullBoard(){
        blackPieces = 0b0000000000000000000000000000000000000000000000L;
        whitePieces = 0b0000000000000000000000000000000000000000000000L;
        kings =       0b0000000000000000000000000000000000000000000000L;
    } //sets up a board with no pieces on, the players return this as their best move if they cannot play
    private void copyBoard(Board original){
        blackPieces = original.getBlackPieces();
        whitePieces = original.getWhitePieces();
        kings = original.getKings();
    } //sets up a board as a copy of another board

    //methods to return bitboards calculated from the 3 base bitboards used to store the board
    public long blackMen(){
        return blackPieces & ~kings;
    } //black men excluding kings
    public long whiteMen(){
        return whitePieces & ~kings;
    } //white men excluding kings
    long blackKings(){
        return blackPieces & kings;
    } //black kings only
    long whiteKings(){
        return whitePieces & kings;
    } //white kings only
    private long allPieces(){
        return blackPieces | whitePieces;
    } //all pieces, black and white
    private long emptySquares() {
        return ~allPieces() & maskValid;
    } //empty squares (valid squares not black nor white)

    //methods to query the board
    boolean isBlack(int position){
        return ((findMask(position) & blackPieces) != 0);
    } //returns true if a given position has a black piece
    boolean isWhite(int position){
        return ((findMask(position) & whitePieces) != 0);
    } //returns true if a given position has a white piece
    boolean isKing(int position){
        return ((findMask(position) & kings) != 0);
    }  //returns true if a given position has a king
    boolean isEmpty(int position){
        return (!(isBlack(position)) && !(isWhite(position)) && isValid(position));
    } //returns true if a given position is valid but has no black or white pieces
    int blackCount(long mask){
        return Long.bitCount(blackPieces & mask);
    } //returns the number of black pieces under a given mask
    int whiteCount(long mask){
        return Long.bitCount(whitePieces & mask);
    } //returns the number of white pieces under a given mask
    public int allCount(long mask){
        return Long.bitCount(allPieces() & mask);
    } //returns the number of pieces, black or white, under a given mask

    //moves one piece, either sliding or a single capture (NO multiple captures) (returns new board, leaves old board)
    Board makeSimpleMove(int source, int destination){

        Board afterMove = new Board();
        afterMove.copyBoard(this);

        //if gap between source and destination, remove piece in middle
        if ((source-destination)*(source-destination) > 25) {
            int captured = (source + destination) / 2;
            if (isBlack(source)){
                afterMove.whitePieces -= findMask(captured);
            } else {
                afterMove.blackPieces -= findMask(captured);
            }
            if (isKing(captured)){ //if captured piece is king, king bit must be removed
                afterMove.kings -= findMask(captured);
            }
        }

        //remove piece from source and replace at destination
        if (isBlack(source)){
            afterMove.blackPieces -= findMask(source);
            afterMove.blackPieces += findMask(destination);
            if (destination >= 37 && !(afterMove.isKing(source))){ //if at end and counter's not a king already, make a king
                afterMove.kings += findMask(destination);
            }
        } else {
            afterMove.whitePieces -= findMask(source);
            afterMove.whitePieces += findMask(destination);
            if (destination <= 8 && !(afterMove.isKing(source))){ //if at end and counter's not a king already, make a king
                afterMove.kings += findMask(destination);
            }
        }
        if (isKing(source)){ //if king, king bit indicator must move too
            afterMove.kings -= findMask(source);
            afterMove.kings += findMask(destination);
        }

        return afterMove;
    }  //...DONE //TESTED

    //finds all legal moves for a given player.
    Board[] findMoves(boolean isBlack){

        long jumpersLF;
        long jumpersRF;
        long jumpersLB;
        long jumpersRB;
        long jumpers;

        if (isBlack) {
            jumpersLF = (emptySquares() << 4 & whitePieces) << 4 & blackPieces; //all black pieces that can jump LF
            jumpersRF = (emptySquares() << 5 & whitePieces) << 5 & blackPieces; //all black pieces that can jump RF
            jumpersLB = (emptySquares() >> 5 & whitePieces) >> 5 & blackKings(); //all black kings that can jump LB
            jumpersRB = (emptySquares() >> 4 & whitePieces) >> 4 & blackKings(); //all black kings that can jump RB
        } else {
            jumpersLF = (emptySquares() << 4 & blackPieces) << 4 & whiteKings(); //all white kings that can jump LF
            jumpersRF = (emptySquares() << 5 & blackPieces) << 5 & whiteKings(); //all white kings that can jump RF
            jumpersLB = (emptySquares() >> 5 & blackPieces) >> 5 & whitePieces; //all white pieces that can jump LB
            jumpersRB = (emptySquares() >> 4 & blackPieces) >> 4 & whitePieces; //all white pieces that can jump RB
        }
        jumpers = jumpersLF | jumpersRF | jumpersLB | jumpersRB; //all pieces which can jump

        if (jumpers != 0) { //if at least one piece can jump

            Board[] totalMoves = new Board[100]; //all possible moves on this board
            int totalMoveCount = 0; //keeps track of total moves
            Board[] furtherMoves; //all possible moves by a given piece

            for (int position = 5; position <= 40; position++) { //go through all board positions
                if ((jumpersLF & findMask(position)) != 0) { //if it can jump LF
                    Board afterJump = this.makeSimpleMove(position, position+8); //make the jump
                    if (!(afterJump.isKing(position+8)) && position >= 28){ //if piece is a man and on back row
                        afterJump.kings += 1L<<(long)(45-(position+8)); //make piece a king
                        totalMoves[totalMoveCount] = afterJump;
                        totalMoveCount ++;
                    } else { //only check for multijumps if didnt become a king
                        furtherMoves = afterJump.findMultiJump(position + 8); //check for all further moves from that position
                        System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length); //add moves to total
                        totalMoveCount += furtherMoves.length; //add number of possible jumps to the total move count
                    }
                }
                if ((jumpersRF & findMask(position)) != 0) { //if it can jump RF
                    Board afterJump = this.makeSimpleMove(position, position+10); //make the jump
                    if (!(afterJump.isKing(position+10)) && position >= 28){ //if piece is a man and on back row
                        afterJump.kings += 1L<<(long)(45-(position+10)); //make piece a king
                        totalMoves[totalMoveCount] = afterJump;
                        totalMoveCount ++;
                    } else { //only check for multijumps if didnt become a king
                        furtherMoves = afterJump.findMultiJump(position + 10); //check for all further moves from that position
                        System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length); //add moves to total
                        totalMoveCount += furtherMoves.length; //add number of possible jumps to the total move count
                    }
                }
                if ((jumpersLB & Board.findMask(position)) != 0) { //if it can jump LB
                    Board afterJump = this.makeSimpleMove(position, position-10); //make the jump
                    if (!(afterJump.isKing(position-10)) && position <= 17){ //if piece is a man and on back row
                        afterJump.kings += 1L<<(long)(45-(position-10)); //make piece a king
                        totalMoves[totalMoveCount] = afterJump;
                        totalMoveCount ++;
                    } else { //only check for multijumps if didnt become a king
                        furtherMoves = afterJump.findMultiJump(position - 10); //check for all further moves from that position
                        System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length); //add moves to total
                        totalMoveCount += furtherMoves.length; //add number of possible jumps to the total move count
                    }
                }
                if ((jumpersRB & Board.findMask(position)) != 0) { //if it can jump RB
                    Board afterJump = this.makeSimpleMove(position, position-8); //make the jump
                    if (!(afterJump.isKing(position-8)) && position <= 17){ //if piece is a man and on back row
                        afterJump.kings += 1L<<(long)(45-(position-8)); //make piece a king
                        totalMoves[totalMoveCount] = afterJump;
                        totalMoveCount ++;
                    } else { //only check for multijumps if didnt become a king
                        furtherMoves = afterJump.findMultiJump(position - 8); //check for all further moves from that position
                        System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length); //add moves to total
                        totalMoveCount += furtherMoves.length; //add number of possible jumps to the total move count
                    }
                }
            }

            totalMoves = trim(totalMoves);
            return totalMoves;

        } else {

            long moversLF;
            long moversRF;
            long moversLB;
            long moversRB;

            if (isBlack) {
                moversLF = emptySquares() << 4 & blackPieces; //all black pieces that can move LF
                moversRF = emptySquares() << 5 & blackPieces; //all black pieces that can move RF
                moversLB = emptySquares() >> 5 & blackKings(); //all black pieces that can move LB
                moversRB = emptySquares() >> 4 & blackKings(); //all black pieces that can move RB
            } else {
                moversLF = emptySquares() << 4 & whiteKings(); //all white pieces that can move LF
                moversRF = emptySquares() << 5 & whiteKings(); //all white pieces that can move RF
                moversLB = emptySquares() >> 5 & whitePieces; //all white pieces that can move LB
                moversRB = emptySquares() >> 4 & whitePieces; //all white pieces that can move RB
            }

            Board[] totalMoves = new Board[100]; //all possible moves on this board
            int totalMoveCount = 0; //keeps track of total moves

            for (int position = 5; position <= 40; position++){ //for each board position
                if ((moversLF & Board.findMask(position)) != 0){ //if it can move LF
                    totalMoves[totalMoveCount] = this.makeSimpleMove(position, position+4); //add move to total
                    totalMoveCount += 1; //move counter increases
                }
                if ((moversRF & Board.findMask(position)) != 0){ //if it can move RF
                    totalMoves[totalMoveCount] = this.makeSimpleMove(position, position+5); //add move to total
                    totalMoveCount += 1; //move counter increases
                }
                if ((moversLB & Board.findMask(position)) != 0){ //if it can move LB
                    totalMoves[totalMoveCount] = this.makeSimpleMove(position, position-5); //add move to total
                    totalMoveCount += 1; //move counter increases
                }
                if ((moversRB & Board.findMask(position)) != 0){ //if it can move RB
                    totalMoves[totalMoveCount] = this.makeSimpleMove(position, position-4); //add move to total
                    totalMoveCount += 1; //move counter increases
                }
            }

            totalMoves = trim(totalMoves);
            return totalMoves;
        }

    } //...DONE //TESTED

    //looks at a single piece which has just jumped, and returns all possible multi jumps it can do afterwards as well as the single jump
    Board[] findMultiJump(int position){
        long positionMask = findMask(position);

        boolean jumpLF;
        boolean jumpRF;
        boolean jumpLB;
        boolean jumpRB;

        if (isBlack(position)){ //is piece black
            jumpLB = false; //assume can't jump backwards
            jumpRB = false;
            if ((positionMask & blackKings()) != 0) { //is piece king, check if can jump backwards
                jumpLB = (positionMask & (emptySquares() >> 5 & whitePieces) >> 5) != 0; //true if piece can jump LB
                jumpRB = (positionMask & (emptySquares() >> 4 & whitePieces) >> 4) != 0; //true if piece can jump RB
            }
            jumpLF = (positionMask & (emptySquares() << 4 & whitePieces) << 4) != 0; //true if piece can jump LF
            jumpRF = (positionMask & (emptySquares() << 5 & whitePieces) << 5) != 0; //true if piece can jump RF

        } else { //piece is therefore white
            jumpLF = false; //assume can't jump backwards
            jumpRF = false;
            if ((positionMask & whiteKings()) != 0) { //is piece king, check if can jump backwards
                jumpLF = (positionMask & (emptySquares() << 4 & blackPieces) << 4) != 0; //true if piece can jump LF
                jumpRF = (positionMask & (emptySquares() << 5 & blackPieces) << 5) != 0; //true if piece can jump RF
            }
            jumpLB = (positionMask & (emptySquares() >> 5 & blackPieces) >> 5) != 0; //true if piece can jump LB
            jumpRB = (positionMask & (emptySquares() >> 4 & blackPieces) >> 4) != 0; //true if piece can jump RB
        }

        if (!(jumpLF | jumpRF | jumpLB | jumpRB)){ //pre-check; if no jumps possible then skip the individual checks to save time (most jumps aren't double jumps)
            return new Board[]{this};
        }

        Board[] totalMoves = new Board[100]; //all possible moves in a given direction using this piece
        totalMoves[0] = this; //the piece is guaranteed to be able to make at least the first jump
        int totalMoveCount = 1; //keeps track of total moves
        Board[] furtherMoves; //all possible further moves in a given direction (double jumps, triple jumps etc)
        Board furtherBoard; //the board state after doing the next stage of the jump

        // if the piece can jump in a direction, set furtherBoard to the result, recursively find all moves from that new position, and add them to the total moves
        if (jumpLF){
            furtherBoard = this.makeSimpleMove(position, position+8);
            if (this.kings < furtherBoard.kings){
                furtherMoves = new Board[]{furtherBoard};
            } else {
                furtherMoves = furtherBoard.findMultiJump(position + 8);
            }
            System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length);
            totalMoveCount += furtherMoves.length;
        }
        if (jumpRF){
            furtherBoard = this.makeSimpleMove(position, position+10);
            if (this.kings < furtherBoard.kings){
                furtherMoves = new Board[]{furtherBoard};
            } else {
                furtherMoves = furtherBoard.findMultiJump(position + 10);
            }
            System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length);
            totalMoveCount += furtherMoves.length;
        }
        if (jumpLB){
            furtherBoard = this.makeSimpleMove(position, position-10);
            if (this.kings < furtherBoard.kings){
                furtherMoves = new Board[]{furtherBoard};
            } else {
                furtherMoves = furtherBoard.findMultiJump(position - 10);
            }
            System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length);
            totalMoveCount += furtherMoves.length;
        } if (jumpRB){
            furtherBoard = this.makeSimpleMove(position, position-8);
            if (this.kings < furtherBoard.kings){
                furtherMoves = new Board[]{furtherBoard};
            } else {
                furtherMoves = furtherBoard.findMultiJump(position - 8);
            }
            System.arraycopy(furtherMoves, 0, totalMoves, totalMoveCount, furtherMoves.length);
        }

        totalMoves = trim(totalMoves);
        return totalMoves;
    } //...DONE //TESTED

}