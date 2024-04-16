package edu.neu.coe.info6205.mcts.nimgame;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameTest {

    private NimGame game;

    @Before
    public void setUp() {
        game = new NimGame();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayMove_throwsExceptionIfMoveIsInvalid() {
        // Attempt to remove more pieces than are present in the pile
        game.playMove(new NimGameMove(0, 4));
    }

    @Test
    public void testIsGameOver_returnsTrueWhenGameIsOver() {
        // Play valid moves until game is over
        game.playMove(new NimGameMove(0, 3)); // Remove all from pile 1
        game.playMove(new NimGameMove(1, 6)); // Remove all from pile 2
        game.playMove(new NimGameMove(2, 9)); // Remove all from pile 3
        assertTrue(game.isGameOver());
    }

    @Test
    public void testPlayMove_throwsExceptionWhenTryingToRemoveZeroPieces() {
        // Attempt to remove 0 pieces
        try {
            game.playMove(new NimGameMove(0, 0));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("You must remove at least one piece.", e.getMessage());
        }
    }

    @Test
    public void testGetCurrentState_reflectsLatestGameState() {
        // Play a valid move
        game.playMove(new NimGameMove(0, 1));
        // Check the current state of the piles
        assertArrayEquals(new int[]{2, 6, 9}, game.getCurrentState().getPiles());
    }

    // More tests can be added to cover the complete functionality
}
