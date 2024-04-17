package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.nimgame.NimGame;
import edu.neu.coe.info6205.mcts.nimgame.NimGameMove;
import edu.neu.coe.info6205.mcts.nimgame.NimGameNode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NimGameNodeTest {
    private NimGameNode node;
    private State<NimGame> initialState;

    @Before
    public void setup() {
        // Assuming NimGame and its state can be initialized like this
        NimGame game = new NimGame();
        initialState = game.start();  // You need to define this method or adjust according to your implementation
        node = new NimGameNode(initialState, null);
    }

    @Test
    public void testInitialNodeIsLeaf() {
        // Assuming that the start state is not terminal
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
