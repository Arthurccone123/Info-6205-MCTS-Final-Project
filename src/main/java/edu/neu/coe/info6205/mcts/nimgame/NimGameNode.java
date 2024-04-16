package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;
import java.util.ArrayList;
import java.util.Collection;

public class NimGameNode implements Node<NimGame> {
    private final State<NimGame> state;
    private final Node<NimGame> parent;
    private final Collection<Node<NimGame>> children = new ArrayList<>();
    private int wins;
    private int playouts;

    public NimGameNode(State<NimGame> state, Node<NimGame> parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<NimGame> state() {
        return state;
    }

    @Override
    public boolean white() {
        // You might need to adjust this based on how you define players
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<NimGame>> children() {
        return children;
    }

    @Override
    public void addChild(State<NimGame> childState) {
        children.add(new NimGameNode(childState, this));
    }

    @Override
    public void backPropagate() {
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
        this.wins += wins;
    }

    @Override
    public Node<NimGame> getParent() {
        return parent;
    }
}
