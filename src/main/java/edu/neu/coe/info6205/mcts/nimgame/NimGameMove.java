package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;

public class NimGameMove implements Move<NimGame> {

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

    @Override
    public int player() {

        return 1;
    }
}
