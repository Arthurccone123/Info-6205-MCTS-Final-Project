package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;

public class NimGameMove implements Move<NimGame> {
    private final int pileIndex;
    private final int numberOfPieces;  // Number of stones to be removed

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

    // Gets the number of stones reduced by this move
    public int getPileReduction() {
        return numberOfPieces;
    }

    @Override
    public int player() {
        // Identifies the player who made the move.
        return 1;
    }
}
