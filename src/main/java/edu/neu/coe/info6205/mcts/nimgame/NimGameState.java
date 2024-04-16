package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.core.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class NimGameState implements State<NimGame> {

    private final int[] piles;
    private final Random random = new Random();

    public NimGameState(int[] piles) {
        this.piles = piles.clone();
    }

    @Override
    public NimGame game() {
        return new NimGame();
    }

    @Override
    public boolean isTerminal() {
        for (int pile : piles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;
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
        return new NimGameState(newPiles);
    }

    @Override
    public int player() {
        return 1;
    }

    @Override
    public Optional<Integer> winner() {
        if (isTerminal()) {
            return Optional.of(1 - player());
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
}
