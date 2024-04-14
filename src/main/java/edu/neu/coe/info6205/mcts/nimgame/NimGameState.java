package edu.neu.coe.info6205.mcts.nimgame;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of the Nim-like game.
 */
public class NimGameState {

    private final int[] piles;

    public NimGameState(int[] piles) {
        this.piles = piles.clone();
    }

    public boolean isGameOver() {
        for (int pile : piles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;
    }

    public List<NimGameMove> getAvailableMoves() {
        List<NimGameMove> moves = new ArrayList<>();
        for (int i = 0; i < piles.length; i++) {
            for (int numToRemove = 1; numToRemove <= piles[i]; numToRemove++) {
                moves.add(new NimGameMove(i, numToRemove));
            }
        }
        return moves;
    }

    public void performMove(NimGameMove move) {
        if (piles[move.getPileIndex()] >= move.getNumberOfPieces()) {
            piles[move.getPileIndex()] -= move.getNumberOfPieces();
        } else {
            throw new IllegalArgumentException("Invalid move.");
        }
    }
    public int[] getPiles() {
        return piles.clone();
    }
}
