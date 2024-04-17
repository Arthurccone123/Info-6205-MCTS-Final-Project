package edu.neu.coe.info6205.mcts.nimgame;

import static org.junit.Assert.*;

import edu.neu.coe.info6205.mcts.core.Node;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

public class MctsTest {
    private MCTS mcts;
    private Node<NimGame> rootNode;

    @Before
    public void setUp() {
        // Initialize the game state and root node
        NimGame game = new NimGame();
        rootNode = new NimGameNode(game.start(), null);
        mcts = new MCTS(rootNode);
    }

    @Test
    public void testSelect() {
        // Assuming initial game state is not a terminal state
        Node<NimGame> selectedNode = mcts.runMCTS(1);  // Run MCTS for one iteration to force selection
        assertNotNull("Select method should not return null", selectedNode);
    }

    @Test
    public void testExpand() {
        // Test if a node is properly expanded
        Node<NimGame> expandedNode = mcts.expand(rootNode);
        assertFalse("Expanded node should have children", expandedNode.children().isEmpty());
    }

    @Test
    public void testSimulate() {
        // Test simulation from a random mid-game node
        Node<NimGame> testNode = mcts.expand(rootNode);  // Get a node to test
        int simulationResult = mcts.simulate(testNode);
        assertTrue("Simulation should return a valid game result", simulationResult == 0 || simulationResult == 1);
    }

    @Test
    public void testBackPropagate() {
        // Expand and simulate a node to set up a backpropagation test
        Node<NimGame> expandedNode = mcts.expand(rootNode);
        int result = mcts.simulate(expandedNode);
        mcts.backPropagate(expandedNode, result);

        // Check if root node playouts and wins have been incremented correctly
        assertEquals("Root node playouts should be incremented", 1, rootNode.playouts());
        if (result == rootNode.state().player()) {
            assertEquals("Root node wins should be incremented", 1, rootNode.wins());
        }
    }

    @Test
    public void testRunMCTS() {
        // Test the overall MCTS functionality
        Node<NimGame> bestNode = mcts.runMCTS(10);  // Run MCTS for a reasonable number of iterations
        assertNotNull("RunMCTS should return a best node", bestNode);
        assertTrue("Best node should have at least one win", bestNode.wins() > 0);
    }

    @Test
    public void testUCTValueCalculation() {
        // Ensure the UCT value is calculated correctly
        Node<NimGame> childNode = mcts.expand(rootNode);
        childNode.incrementPlayouts();
        childNode.addWins(1);
        double uctValue = mcts.uctValue(childNode, 10);  // Total playouts set to 10 for testing
        assertTrue("UCT value should be a valid double", uctValue > 0);
    }
}
