package edu.neu.coe.info6205.mcts.nimgame;

import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameMoveTest {

    @Test
    public void testGetPileIndex() {
        // To ensure getPileIndex returns the correct index
        NimGameMove move = new NimGameMove(1, 3);
        assertEquals(1, move.getPileIndex());
    }

    @Test
    public void testGetNumberOfPieces() {
        // To ensure getNumberOfPieces returns the correct number of pieces
        NimGameMove move = new NimGameMove(1, 3);
        assertEquals(3, move.getNumberOfPieces());
    }

}
