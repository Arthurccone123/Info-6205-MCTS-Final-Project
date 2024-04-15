package edu.neu.coe.info6205.mcts.tictactoe;

import edu.neu.coe.info6205.mcts.core.Node;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MCTSTest {
    private TicTacToe game;
    private Node<TicTacToe> rootNode;
    private MCTS mcts;

    @Before
    public void setUp() {
        game = new TicTacToe();  // Assuming your TicTacToe has a default constructor setting up the game
        rootNode = new TicTacToeNode(game.start(), null);  // Start the game state and no parent
        mcts = new MCTS(rootNode);
    }

    @Test
    public void testInitialization() {
        assertNotNull("MCTS initialization failed, root node is null", mcts);
    }

    @Test
    public void testRunMCTS() {
        Node<TicTacToe> bestNode = mcts.runMCTS(100);
        assertNotNull("MCTS runMCTS method failed, no best node found", bestNode);
    }

    @Test
    public void testSelectMethod() {
        Node<TicTacToe> selectedNode = mcts.select(rootNode);
        assertNotNull("MCTS select method failed, no node selected", selectedNode);
    }

    @Test
    public void testExpandMethod() {
        // Ensure the root node is not a leaf to allow expansion
        if (!rootNode.isLeaf()) {
            Node<TicTacToe> expandedNode = mcts.expand(rootNode);
            assertNotNull("MCTS expand method failed, no node expanded", expandedNode);
        }
    }

    @Test
    public void testSimulation() {
        int result = mcts.simulate(rootNode.state());
        assertNotEquals("MCTS simulation failed, result should not be -1 indicating no winner in non-terminal state", -1, result);
    }

    @Test
    public void testBackPropagation() {
        // Run a simulation to create a scenario for backpropagation
        Node<TicTacToe> node = mcts.runMCTS(10);
        mcts.backPropagate(node, node.state().winner().orElse(-1));
        assertTrue("Backpropagation failed to update playouts", node.playouts() > 0);
    }
}