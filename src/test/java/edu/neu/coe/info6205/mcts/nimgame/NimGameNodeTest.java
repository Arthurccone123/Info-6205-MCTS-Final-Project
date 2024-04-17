package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.State;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameNodeTest {
    private NimGameNode node;
    private State<NimGame> initialState;

    @Before
    public void setup() {
        NimGame game = new NimGame();
        initialState = game.start();
        node = new NimGameNode(initialState, null);
    }

    @Test
    public void testInitialNodeIsLeaf() {
        assertFalse(node.isLeaf());
    }

    @Test
    public void testInitialState() {
        assertSame("Node state should be the initial state", initialState, node.state());
    }

    @Test
    public void testAddChild() {
        State<NimGame> newState = initialState.next(new NimGameMove(0, 1));  // Adjust parameters as necessary
        node.addChild(newState);
        assertFalse("Children should not be empty after adding one", node.children().isEmpty());
        assertEquals("Should have one child", 1, node.children().size());
    }

    @Test
    public void testBackPropagation() {
        node.addChild(initialState.next(new NimGameMove(0, 1)));
        node.addChild(initialState.next(new NimGameMove(1, 2)));

        node.children().forEach(child -> {
            ((NimGameNode) child).incrementPlayouts();
            ((NimGameNode) child).addWins(1);
        });

        node.backPropagate();

        assertEquals("Playouts should be equal to the number of children", 2, node.playouts());
        assertEquals("Wins should be equal to the number of children", 2, node.wins());
    }

    @Test
    public void testIncrementPlayoutsAndAddWins() {
        int initialPlayouts = node.playouts();
        int initialWins = node.wins();
        node.incrementPlayouts();
        node.addWins(1);

        assertEquals("Playouts should increment by 1", initialPlayouts + 1, node.playouts());
        assertEquals("Wins should increment by 1", initialWins + 1, node.wins());
    }
}
