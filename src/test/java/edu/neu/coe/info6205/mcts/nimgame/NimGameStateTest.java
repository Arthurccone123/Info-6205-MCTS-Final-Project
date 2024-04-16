package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NimGameStateTest {

    @Test
    public void testIsTerminal() {
        // Assuming isTerminal returns true when all piles are empty
        NimGameState gameState = new NimGameState(new int[]{0, 0, 0});
        assertTrue(gameState.isTerminal());
    }

    @Test
    public void testMoves() {
        // Assuming moves should return all possible moves
        NimGameState gameState = new NimGameState(new int[]{1, 2, 3});
        List<Move<NimGame>> moves = gameState.moves(0); // Assuming player 0's turn
        assertEquals(6, moves.size()); // There should be 6 possible moves
    }

    @Test
    public void testNext() {
        // Assuming next correctly calculates the next state
        NimGameState gameState = new NimGameState(new int[]{1, 2, 3});
        NimGameMove move = new NimGameMove(0, 1); // Remove 1 from the first pile
        NimGameState nextState = gameState.next(move);
        assertArrayEquals(new int[]{0, 2, 3}, nextState.getPiles());
    }

    // Add more tests for other methods
}
