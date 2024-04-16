package edu.neu.coe.info6205.mcts.nimgame;

import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameMoveTest {

    @Test
    public void testGetPileIndex() {
        // Test to ensure getPileIndex returns the correct index
        NimGameMove move = new NimGameMove(1, 3);
        assertEquals(1, move.getPileIndex());
    }

    @Test
    public void testGetNumberOfPieces() {
        // Test to ensure getNumberOfPieces returns the correct number of pieces
        NimGameMove move = new NimGameMove(1, 3);
        assertEquals(3, move.getNumberOfPieces());
    }

    // Add more tests for other methods if necessary
}
