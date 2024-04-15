package edu.neu.coe.info6205.mcts.tictactoe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicTacToeNodeTest {

    // update winsAndPlayouts(), state(), white() , since the TicTacToeNode constructor is changed.
    @Test
    public void winsAndPlayouts() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState(Position.parsePosition("X . 0\nX O .\nX . 0", TicTacToe.X));
        TicTacToeNode node = new TicTacToeNode(state, null);  // Pass `null` for parent if not required for the test
        assertTrue(node.isLeaf());
        assertEquals(2, node.wins());
        assertEquals(1, node.playouts());
    }

    @Test
    public void state() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state, null);  // Again, passing `null` for parent
        assertEquals(state, node.state());
    }

    @Test
    public void white() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state, null);
        assertTrue(node.white());
    }



    @Test
    public void children() {
        // no tests yet
    }

    @Test
    public void addChild() {
        // no tests yet
    }

    @Test
    public void backPropagate() {
        // no tests yet
    }
}