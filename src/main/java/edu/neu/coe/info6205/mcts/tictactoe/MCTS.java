package edu.neu.coe.info6205.mcts.tictactoe;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
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
        Move<TicTacToe> move = moves.get(random.nextInt(moves.size()));
        State<TicTacToe> newState = node.state().next(move);

        Node<TicTacToe> newNode = new TicTacToeNode(newState, node);
        node.children().add(newNode);
        return newNode;
    }

    private int simulate(State<TicTacToe> state) {
        State<TicTacToe> tempState = state;
        Random localRandom = new Random();
        while (!tempState.isTerminal()) {
            List<Move<TicTacToe>> possibleMoves = new ArrayList<>(tempState.moves(tempState.player()));
            if (possibleMoves.isEmpty()) {
                break;
            }
            Move<TicTacToe> selectedMove = possibleMoves.get(localRandom.nextInt(possibleMoves.size()));
            tempState = tempState.next(selectedMove);
        }
        return tempState.winner().orElse(-1);
    }


    private void backPropagate(Node<TicTacToe> node, int result) {
        while (node != null) {
            node.incrementPlayouts();
            if (result != -1 && node.state().player() == result) {
                node.addWins(1);  // Assumes 1 point per win
            }
            node = node.getParent();
        }
    }

    private Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparing(Node::wins))
                .orElseThrow(() -> new IllegalStateException("No children nodes found"));
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        Node<TicTacToe> root = new TicTacToeNode(game.start(), null);
        MCTS mcts = new MCTS(root);
        Node<TicTacToe> bestNode = mcts.runMCTS(1000);


        System.out.println("Initial Board State:");
        System.out.println(root.state());


        System.out.println("\nBest node state after 1000 simulations:");
        System.out.println(bestNode.state());


        double winRate = bestNode.wins() / (double) bestNode.playouts() * 100;
        System.out.println("Win rate of best node: " + String.format("%.2f%%", winRate));
    }
}