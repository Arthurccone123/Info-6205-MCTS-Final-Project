package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;
import java.util.ArrayList;
import java.util.Collection;

public class NimGameNode implements Node<NimGame> {
    private final State<NimGame> state;
    private final Node<NimGame> parent;
    private final Collection<Node<NimGame>> children = new ArrayList<>();
    private int wins;        // Number of wins accumulated in this node
    private int playouts;    // Total number of playouts from this node

    public NimGameNode(State<NimGame> state, Node<NimGame> parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        // Determines if the node is a leaf node
        return state.isTerminal();
    }

    @Override
    public State<NimGame> state() {
        return state;
    }

    @Override
    public boolean white() {
        // Determines if the current player is the opening player
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<NimGame>> children() {
        return children;
    }

    @Override
    public void addChild(State<NimGame> childState) {
        // Adds a child to this node based on the provided state
        children.add(new NimGameNode(childState, this));
    }

    @Override
    public void backPropagate() {
        // Update the playouts and wins based on the children's statistics
        playouts = children.stream().mapToInt(Node::playouts).sum();
        wins = children.stream().mapToInt(Node::wins).sum();
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    @Override
    public void incrementPlayouts() {
        playouts++;
    }

    @Override
    public void addWins(int wins) {
        // Adds the specified number of wins to this node's total
        this.wins += wins;
    }

    @Override
    public Node<NimGame> getParent() {
        return parent;
    }
}
