package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void testCurrentPlayerAfterMove() {
        NimGameState gameState = new NimGameState(new int[]{1, 2, 3});
        Move<NimGame> move = new NimGameMove(0, 1);
        NimGameState nextState = gameState.next(move);
        assertEquals("Current player should switch after a move", 0, nextState.getCurrentPlayer());
    }

    @Test
    public void testWinnerAtEndOfGame() {
        NimGameState terminalState = new NimGameState(new int[]{0, 0, 0});
        Optional<Integer> winner = terminalState.winner();
        assertTrue("Winner should be present at the end of the game", ((Optional<?>) winner).isPresent());
        assertEquals("Incorrect winner", 0, winner.get().intValue());
    }

    @Test
    public void testNonTerminalState() {
        NimGameState gameState = new NimGameState(new int[]{1, 0, 0});
        assertFalse("Game should not be terminal", gameState.isTerminal());
    }

    @Test
    public void testMovesForDifferentPlayers() {
        NimGameState gameStatePlayer0 = new NimGameState(new int[]{1, 2, 3});
        gameStatePlayer0.setCurrentPlayer(0);
        List<Move<NimGame>> movesPlayer0 = gameStatePlayer0.moves(0);
        assertEquals("Incorrect number of moves for player 0", 6, movesPlayer0.size());

        NimGameState gameStatePlayer1 = new NimGameState(new int[]{1, 2, 3});
        gameStatePlayer1.setCurrentPlayer(1);
        List<Move<NimGame>> movesPlayer1 = gameStatePlayer1.moves(1);
        assertEquals("Incorrect number of moves for player 1", 6, movesPlayer1.size());
    }

    // Add more tests for other methods
}
