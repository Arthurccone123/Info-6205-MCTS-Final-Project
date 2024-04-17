package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.core.Move;
import java.util.*;

public class NimGameState implements State<NimGame> {

    private final int[] piles; // Array representing the number of stones in each pile
    private final Random random = new Random();
    private int currentPlayer; // Current player's identifier

    public NimGameState(int[] piles, int currentPlayer) {
        this.piles = piles.clone(); // Ensure a deep copy to prevent mutation of the original array
        this.currentPlayer = currentPlayer;
    }

    @Override
    public boolean isTerminal() {
        // Checks if the game state is terminal, i.e., all piles are empty
        for (int pile : piles) {
            if (pile > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Move<NimGame>> moves(int player) {
        // Generates all possible moves for the current state
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
        // Returns the new state resulting from applying a move
        int[] newPiles = piles.clone();
        NimGameMove nimMove = (NimGameMove) move;
        newPiles[nimMove.getPileIndex()] -= nimMove.getNumberOfPieces();
        int nextPlayer = 1 - currentPlayer; // Switch players
        return new NimGameState(newPiles, nextPlayer);
    }

    @Override
    public int player() {

        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {

        if (isTerminal()) {
            // The player who cannot make a move loses, hence the other player is the winner
            return Optional.of(1 - currentPlayer);
        }
        return Optional.empty(); // No winner yet if the game isn't over
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
