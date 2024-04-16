package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Node;
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
        NimGameState childState = new NimGameState(new int[]{2, 3, 3});
        NimGameNode childNode = new NimGameNode(childState, node);
        node.children().add(childNode);

        int simulatedResult = 1;

        childNode.incrementPlayouts();
        if (simulatedResult == childState.player()) {
            childNode.addWins(1);
        }
        node.backPropagate();

        assertEquals(1, node.playouts());
        assertEquals(simulatedResult == initialState.player() ? 1 : 0, node.wins());
    }

    @Test
    public void testBackPropagateWithMultipleChildren() {
        // Set up children and simulate their outcomes
        NimGameNode childNode1 = new NimGameNode(new NimGameState(new int[]{2, 3, 3}), node);
        NimGameNode childNode2 = new NimGameNode(new NimGameState(new int[]{3, 2, 3}), node);
        node.children().add(childNode1);
        node.children().add(childNode2);

        childNode1.incrementPlayouts();
        childNode2.incrementPlayouts();
        childNode1.addWins(1); // simulate a win for childNode1
        node.backPropagate();

        assertEquals(2, node.playouts());
        assertEquals(1, node.wins());
    }

    @Test
    public void testBackPropagateWithDeepTree() {
        // Set up a tree with multiple levels
        NimGameNode childNode = new NimGameNode(new NimGameState(new int[]{2, 3, 3}), node);
        NimGameNode grandChildNode = new NimGameNode(new NimGameState(new int[]{2, 2, 3}), childNode);
        node.children().add(childNode);
        childNode.children().add(grandChildNode);

        grandChildNode.incrementPlayouts();
        grandChildNode.addWins(1); // simulate a win for grandChildNode
        childNode.backPropagate();
        node.backPropagate();

        assertEquals(1, node.playouts());
        assertEquals(1, node.wins()); // Assuming initialState.player() is not the winner
        assertEquals(1, childNode.wins());
    }

    @Test
    public void testIncrementWinsAndPlayouts() {
        node.incrementPlayouts();
        node.addWins(1);
        assertEquals(1, node.wins());
        assertEquals(1, node.playouts());
    }

    @Test
    public void testAddChildCreatesCorrectParentLink() {
        NimGameState childState = new NimGameState(new int[]{2, 3, 3});
        node.addChild(childState);
        Node<NimGame> childNode = node.children().iterator().next();
        assertEquals(node, childNode.getParent());
    }

    @Test
    public void testParentLink() {
        NimGameNode parentNode = new NimGameNode(new NimGameState(new int[]{3, 3, 3}), null);
        NimGameNode childNode = new NimGameNode(initialState, parentNode);
        assertEquals(parentNode, childNode.getParent());
    }

    @Test
    public void testWhiteCondition() {
        // Assuming that the opener is player 0 and player() method returns current player
        boolean isWhite = node.white();
        assertEquals(initialState.game().opener() == initialState.player(), isWhite);
    }

// Add more tests as needed to cover all aspects of your
}