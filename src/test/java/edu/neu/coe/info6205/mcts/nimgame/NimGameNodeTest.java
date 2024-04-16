package edu.neu.coe.info6205.mcts.nimgame;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameNodeTest {

    private NimGameNode node;
    private NimGameState initialState;

    @Before
    public void setUp() {
        initialState = new NimGameState(new int[]{3, 3, 3});
        node = new NimGameNode(initialState, null);
    }

    @Test
    public void testIsLeaf_initialStateNotTerminal() {
        assertFalse(node.isLeaf());
    }

    @Test
    public void testIsLeaf_terminalState() {
        NimGameState terminalState = new NimGameState(new int[]{0, 0, 0});
        NimGameNode terminalNode = new NimGameNode(terminalState, null);
        assertTrue(terminalNode.isLeaf());
    }

    @Test
    public void testAddChild() {
        NimGameState childState = new NimGameState(new int[]{2, 3, 3});
        NimGameNode childNode = new NimGameNode(childState, node);
        node.addChild(childState);

        assertEquals(1, node.children().size());
    }

    @Test
    public void testBackPropagate() {
        // Step 1: Set up a simple tree for the test with a clear path to simulate.
        NimGameState childState = new NimGameState(new int[]{2, 3, 3}); // Create a child state
        NimGameNode childNode = new NimGameNode(childState, node); // Create a child node with the parent being `node`
        node.children().add(childNode); // Add the child node to the parent's children

        // Step 2 & 3: Simulate a game outcome where the child node is the end state and the result is a win.
        int simulatedResult = 1; // Assuming player 1 is the winner in this simulation

        // Step 4: Backpropagate the result from the child node to the root node.
        childNode.incrementPlayouts(); // Simulate that the child node was part of a play
        if (simulatedResult == childState.player()) {
            childNode.addWins(1); // Simulate a win for the child node
        }
        node.backPropagate(); // Now call backPropagate on the parent node to update its stats

        // Step 5: Assert that the playouts and wins have been updated correctly.
        assertEquals(1, node.playouts()); // The parent node should have 1 playout now
        assertEquals(simulatedResult == initialState.player() ? 1 : 0, node.wins()); // The parent node should have 1 win if it represents the winner
    }

// Add more tests as needed to cover all aspects of your
}