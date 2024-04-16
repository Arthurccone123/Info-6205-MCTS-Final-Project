package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Game;
import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;

import java.util.*;
import java.util.stream.Collectors;

public class MCTS_Pure {
    private final Node<NimGame> root;
    private final static double EXPLORATION_CONSTANT = Math.sqrt(2);
    private final Random random = new Random();

    public MCTS_Pure(Node<NimGame> root) {
        this.root = root;
    }

    public Node<NimGame> runMCTS(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Node<NimGame> selectedNode = select(root);
            if (!selectedNode.isLeaf()) {
                selectedNode = expand(selectedNode);
            }
            int simulationResult = simulate(selectedNode);
            backPropagate(selectedNode, simulationResult);
        }
        return bestChild(root);
    }

    private Node<NimGame> select(Node<NimGame> node) {
        Node<NimGame> currentNode = node;
        while (!currentNode.isLeaf()) {
            if (currentNode.children().isEmpty()) {
                return currentNode;
            } else {
                final int currentPlayouts = currentNode.playouts();
                currentNode = Collections.max(currentNode.children(),
                        Comparator.comparing(c -> uctValue(c, currentPlayouts)));
            }
        }
        return currentNode;
    }

    private double uctValue(Node<NimGame> node, int totalPlayouts) {
        if (node.playouts() == 0) return Double.MAX_VALUE;
        double winRate = node.wins() / (double) node.playouts();
        double logTerm = Math.log(totalPlayouts) / node.playouts();
        return winRate + EXPLORATION_CONSTANT * Math.sqrt(logTerm);
    }

    private Node<NimGame> expand(Node<NimGame> node) {
        List<Move<NimGame>> moves = (List<Move<NimGame>>) node.state().moves(node.state().player());
        Move<NimGame> selectedMove = moves.get(random.nextInt(moves.size()));
        State<NimGame> newState = node.state().next(selectedMove);
        Node<NimGame> newNode = new NimGameNode(newState, node);
        node.children().add(newNode);
        return newNode;
    }

    private int simulate(Node<NimGame> node) {
        Node<NimGame> currentNode = node;
        while (!currentNode.state().isTerminal()) {
            List<Move<NimGame>> moves = (List<Move<NimGame>>) currentNode.state().moves(currentNode.state().player());
            Move<NimGame> selectedMove = moves.get(random.nextInt(moves.size()));
            currentNode = new NimGameNode(currentNode.state().next(selectedMove), currentNode.getParent());
        }
        return currentNode.state().winner().orElse(-1);
    }

    private void backPropagate(Node<NimGame> node, int result) {
        Node<NimGame> currentNode = node;
        while (currentNode != null) {
            currentNode.incrementPlayouts();
            if (result == currentNode.state().player()) {
                currentNode.addWins(1);
            }
            currentNode = currentNode.getParent();
        }
    }

    private Node<NimGame> bestChild(Node<NimGame> node) {
        return Collections.max(node.children(), Comparator.comparing(Node::wins));
    }


}
