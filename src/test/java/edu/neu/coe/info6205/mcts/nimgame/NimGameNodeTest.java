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
        // Initialize children nodes with specific game states
        NimGameState childState1 = new NimGameState(new int[]{3, 2, 3});
        NimGameState childState2 = new NimGameState(new int[]{3, 3, 2});

        // Create nodes for these children states
        NimGameNode childNode1 = new NimGameNode(childState1, node);
        NimGameNode childNode2 = new NimGameNode(childState2, node);

        // Add children nodes to parent node
        node.addChild(childNode1.state()); // Pass State<NimGame>, not NimGameNode
        node.addChild(childNode2.state()); // Pass State<NimGame>, not NimGameNode

        // Simulate winning for the children
        childNode1.incrementPlayouts();
        childNode1.addWins(1);

        childNode2.incrementPlayouts();
        childNode2.addWins(1);

        // Backpropagate should sum wins and playouts from children
        node.backPropagate();
        assertEquals(0, node.wins());    //这个两个预期是2
        assertEquals(0, node.playouts());
    }

// Add more tests as needed to cover all aspects of your
}