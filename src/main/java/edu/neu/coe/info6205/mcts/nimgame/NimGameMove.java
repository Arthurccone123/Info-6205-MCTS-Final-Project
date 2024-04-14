package edu.neu.coe.info6205.mcts.nimgame;

/**
 * This class represents a move in the Nim-like game.
 */
public class NimGameMove {

    private final int pileIndex;
    private final int numberOfPieces;

    public NimGameMove(int pileIndex, int numberOfPieces) {
        this.pileIndex = pileIndex;
        this.numberOfPieces = numberOfPieces;
    }

    public int getPileIndex() {
        return pileIndex;
    }

    public int getNumberOfPieces() {
        return numberOfPieces;
    }


}
