//
// Created by Stuart on 26/02/2017.
//

#include "native-lib.h"

class Board{
    private:
        static long long maskValid = 0b0000011110111111110111111110111111110111100000L; //all the valid board squares (the 0s are padding)
        static long long maskBlackBack = 0b0000011110000000000000000000000000000000000000L; //the home row squares for black
        static long long maskWhiteBack = 0b0000000000000000000000000000000000000111100000L; //the home row squares for white
        static long long maskCenter = 0b0000000000000000000011001100000000000000000000L; //the central 8 squares (4 playable squares)

        long long blackPieces;
        long long whitePieces;
        long long kings;

        long long blackKings(){
            return blackPieces & kings;
        } //black kings only
        long long whiteKings(){
            return whitePieces & kings;
        } //white kings only
        long long emptySquares() {
            return ~allPieces() & maskValid;
        }
        long long allPieces(){
            return blackPieces | whitePieces;
        }


    public:
        Board[] findMoves(boolean isPlayer1, boolean optionalCapture){
            long long jumpersLF;
            long long jumpersRF;
            long long jumpersLB;
            long long jumpersRB;
            long long jumpers;
            if (isPlayer1) {
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
        }



};