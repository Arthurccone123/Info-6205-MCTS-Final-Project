package edu.neu.coe.info6205.mcts.tictactoe;

import org.junit.Test;

import static org.junit.Assert.*;

public class TicTacToeNodeTest {


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
    public void childrenInitiallyEmpty() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state, null);
        assertTrue("Children list should be initially empty", node.children().isEmpty());
    }

    @Test
    public void addChild() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode parentNode = new TicTacToeNode(state, null);
        TicTacToeNode childNode = new TicTacToeNode(state, parentNode);
        parentNode.addChild(state);
        assertFalse("Children list should not be empty after adding a child", parentNode.children().isEmpty());
        assertEquals("Children list should contain the added child", childNode.state(), parentNode.children().iterator().next().state());
    }

    @Test
    public void backPropagate() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode parentNode = new TicTacToeNode(state, null);

        // Creating children with specific win/playout values
        TicTacToeNode child1 = new TicTacToeNode(state, parentNode);
        child1.addWins(3);
        child1.incrementPlayouts();
        child1.incrementPlayouts();

        TicTacToeNode child2 = new TicTacToeNode(state, parentNode);
        child2.addWins(1);
        child2.incrementPlayouts();

        parentNode.addChild(state);
        parentNode.addChild(state);

        // Mimic the addChild setting children directly for testing
        parentNode.children().add(child1);
        parentNode.children().add(child2);

        parentNode.backPropagate();

        assertEquals("Playouts should be sum of children's playouts", 3, parentNode.playouts());
        assertEquals("Wins should be sum of children's wins", 4, parentNode.wins());
    }
}