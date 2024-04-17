package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class NimGameStateTest {
    private NimGameState state;

    @BeforeEach
    public void setup() {
        state = new NimGameState(new int[]{3, 4, 5}, 0);
    }

    @Test
    public void testIsTerminalFalse() {
        assertFalse(state.isTerminal(), "State should not be terminal when there are moves left.");
    }

    @Test
    public void testIsTerminalTrue() {
        state = new NimGameState(new int[]{0, 0, 0}, 0);
        assertTrue(state.isTerminal(), "State should be terminal when no moves left.");
    }

    @Test
    public void testMoves() {
        List<Move<NimGame>> moves = state.moves(state.player());
        int expectedMoves = 3 + 4 + 5; // Sum of all possible moves for each pile
        assertEquals(expectedMoves, moves.size(), "Should generate correct number of moves.");
    }

    @Test
    public void testNextState() {
        Move<NimGame> move = new NimGameMove(0, 2); // Take 2 from pile 0
        State<NimGame> nextState = state.next(move);
        assertNotNull(nextState, "Next state should not be null.");
        assertArrayEquals(new int[]{1, 4, 5}, ((NimGameState) nextState).getPiles(), "Piles should be updated correctly.");
    }

    @Test
    public void testWinner() {
        state = new NimGameState(new int[]{0, 0, 0}, 0); // Assuming last move was made by player 1
        assertTrue(state.winner().isPresent(), "Winner should be present when the game ends.");
        assertEquals(1, state.winner().get(), "Player 1 should be the winner if last move made by player 0 in a terminal state.");
    }

    @Test
    public void testRandom() {
        assertNotNull(state.random(), "Random should not be null.");
    }
}
