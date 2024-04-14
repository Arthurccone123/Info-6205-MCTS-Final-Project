package edu.neu.coe.info6205.mcts.tictactoe;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;

import java.util.*;

public class MCTS {
    private final Node<TicTacToe> root;
    private final Random random = new Random();
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public Node<TicTacToe> runMCTS(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Node<TicTacToe> selectedNode = select(root);
            if (!selectedNode.isLeaf()) {
                selectedNode = expand(selectedNode);
            }
            int simulationResult = simulate(selectedNode.state());
            backPropagate(selectedNode, simulationResult);
        }
        return bestChild(root);
    }

    private Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf()) {
            if (isFullyExpanded(node)) {
                node = bestUCT(node);
            } else {
                return node;
            }
        }
        return node;
    }

    private boolean isFullyExpanded(Node<TicTacToe> node) {
        return node.children().size() >= node.state().moves(node.state().player()).size();
    }

    private Node<TicTacToe> bestUCT(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparing(c -> uctValue(node, c)))
                .orElseThrow(() -> new IllegalStateException("No children nodes found"));
    }

    private double uctValue(Node<TicTacToe> parent, Node<TicTacToe> child) {
        int totalVisits = parent.playouts();
        int winScore = child.wins();
        int numVisits = child.playouts();
        if (numVisits == 0) return Double.MAX_VALUE;
        return (winScore / (double) numVisits) + EXPLORATION_CONSTANT * Math.sqrt(Math.log(totalVisits) / numVisits);
    }

    private Node<TicTacToe> expand(Node<TicTacToe> node) {
        List<Move<TicTacToe>> moves = new ArrayList<>(node.state().moves(node.state().player()));
        // Improve strategy: prefer center or strategic moves
        Move<TicTacToe> selectedMove = moves.get(random.nextInt(moves.size())); // Placeholder for strategic move selection
        State<TicTacToe> newState = node.state().next(selectedMove);
        Node<TicTacToe> newNode = new TicTacToeNode(newState, node);
        node.children().add(newNode);
        return newNode;
    }

    private int simulate(State<TicTacToe> state) {
        State<TicTacToe> tempState = state;
        while (!tempState.isTerminal()) {
            List<Move<TicTacToe>> moves = new ArrayList<>(tempState.moves(tempState.player()));
            Move<TicTacToe> selectedMove = moves.get(random.nextInt(moves.size()));
            tempState = tempState.next(selectedMove);
        }
        return tempState.winner().orElse(-1);
    }

    private void backPropagate(Node<TicTacToe> node, int result) {
        Node<TicTacToe> tempNode = node;
        while (tempNode != null) {
            tempNode.incrementPlayouts();
            if (result == tempNode.state().player()) {
                tempNode.addWins(1);
            }
            tempNode = tempNode.getParent();
        }
    }

    private Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparing(Node::wins))
                .orElseThrow(() -> new IllegalStateException("No children nodes found"));
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        Node<TicTacToe> currentNode = new TicTacToeNode(game.start(), null);
        MCTS mcts = new MCTS(currentNode);

        while (!currentNode.state().isTerminal()) {
            currentNode = mcts.runMCTS(1000);
            System.out.println("Current Board State:");
            System.out.println(currentNode.state());
            System.out.println("Win Probability: " + currentNode.wins() / (double) currentNode.playouts());
            mcts = new MCTS(currentNode);  // Reset the MCTS with the new root
        }

        Optional<Integer> winner = currentNode.state().winner();
        if (winner.isPresent()) {
            System.out.println("Game Over. Winner: " + (winner.get() == 1 ? "X" : "O"));
        } else {
            System.out.println("Game Over. It's a draw.");
        }
    }
}
