package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.core.Move;
import java.util.*;

public class NimGameState implements State<NimGame> {

    private final int[] piles;
    private final Random random = new Random();
    private int currentPlayer;

    public NimGameState(int[] piles, int currentPlayer) {
        this.piles = piles.clone();
        this.currentPlayer = currentPlayer;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public boolean isTerminal() {
        for (int pile : piles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;  // No pieces left to move
    }

    @Override
    public List<Move<NimGame>> moves(int player) {
        List<Move<NimGame>> moves = new ArrayList<>();
        for (int i = 0; i < piles.length; i++) {
            for (int numToRemove = 1; numToRemove <= piles[i]; numToRemove++) {
                moves.add(new NimGameMove(i, numToRemove));
            }
        }
        return moves;
    }

    @Override
    public NimGameState next(Move<NimGame> move) {
        int[] newPiles = piles.clone();
        NimGameMove nimMove = (NimGameMove) move;
        newPiles[nimMove.getPileIndex()] -= nimMove.getNumberOfPieces();
        int nextPlayer = 1 - currentPlayer;
        return new NimGameState(newPiles, nextPlayer);
    }

    @Override
    public int player() {
        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {
        if (isTerminal()) {

            return Optional.of(1 - currentPlayer);
        }
        return Optional.empty();
    }


    @Override
    public Random random() {
        return random;
    }

    public int[] getPiles() {
        return piles.clone();
    }

    @Override
    public NimGame game() {
        return new NimGame();
    }
}
