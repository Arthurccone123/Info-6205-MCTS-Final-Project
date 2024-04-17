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

    // Runs the MCTS algorithm for a specified number of iterations
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

    // Selects a node based on the UCT value if fully expanded, else returns current node for expansion
    Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf()) {
            if (isFullyExpanded(node)) {
                node = bestUCT(node);
            } else {
                return node;
            }
        }
        return node;
    }

    // Checks if all possible moves from a node have been expanded into child nodes
    private boolean isFullyExpanded(Node<TicTacToe> node) {
        return node.children().size() >= node.state().moves(node.state().player()).size();
    }

    // Selects the child node with the highest UCT value
    private Node<TicTacToe> bestUCT(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparing(c -> uctValue(node, c)))
                .orElseThrow(() -> new IllegalStateException("No children nodes found"));
    }

    // Calculates the Upper Confidence Bound for Trees value
    private double uctValue(Node<TicTacToe> parent, Node<TicTacToe> child) {
        int totalVisits = parent.playouts();
        int winScore = child.wins();
        int numVisits = child.playouts();
        if (numVisits == 0) return Double.MAX_VALUE;
        return (winScore / (double) numVisits) + EXPLORATION_CONSTANT * Math.sqrt(Math.log(totalVisits) / numVisits);
    }

    // Expands the tree by adding a new child node created from a selected move
    Node<TicTacToe> expand(Node<TicTacToe> node) {
        List<Move<TicTacToe>> moves = new ArrayList<>(node.state().moves(node.state().player()));
        Move<TicTacToe> selectedMove = selectStrategicMove(moves, node.state());
        State<TicTacToe> newState = node.state().next(selectedMove);
        Node<TicTacToe> newNode = new TicTacToeNode(newState, node);
        node.children().add(newNode);
        return newNode;
    }

    // Selects the most promising move based on a simple heuristic
    private Move<TicTacToe> selectStrategicMove(List<Move<TicTacToe>> moves, State<TicTacToe> state) {
        return moves.stream().max(Comparator.comparing(move -> evaluateMovePotential(move, state))).orElse(null);
    }

    // Evaluates the potential of a move by checking if it leads to a win or random value otherwise
    private double evaluateMovePotential(Move<TicTacToe> move, State<TicTacToe> state) {
        State<TicTacToe> resultState = state.next(move);
        if (resultState.winner().isPresent() && resultState.winner().get() == state.player()) {
            return Double.MAX_VALUE;
        }
        return Math.random();
    }

    // Simulates random play to the end of the game and returns the winner
    int simulate(State<TicTacToe> state) {
        State<TicTacToe> tempState = state;
        while (!tempState.isTerminal()) {
            List<Move<TicTacToe>> moves = new ArrayList<>(tempState.moves(tempState.player()));
            Move<TicTacToe> randomMove = moves.get(random.nextInt(moves.size()));
            tempState = tempState.next(randomMove);
        }
        return tempState.winner().orElse(-1);
    }

    // Back-propagates the simulation results to adjust scores and playout counts
    void backPropagate(Node<TicTacToe> node, int result) {
        Node<TicTacToe> tempNode = node;
        while (tempNode != null) {
            tempNode.incrementPlayouts();
            if (result == tempNode.state().player()) {
                tempNode.addWins(1);
            }
            tempNode = tempNode.getParent();
        }
    }

    // Selects the child node with the highest number of wins
    private Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparing(Node::wins))
                .orElseThrow(() -> new IllegalStateException("No children nodes found"));
    }

    public static void main(String[] args) {
        int winCountAI = 0;
        int winCountRandom = 0;
        int numberOfGames = 1000;
        boolean detailedPrint = true;

        for (int i = 0; i < numberOfGames; i++) {
            TicTacToe game = new TicTacToe();
            Node<TicTacToe> currentNode = new TicTacToeNode(game.start(), null);
            MCTS mcts = new MCTS(currentNode);

            // Games involving MCTS intervention
            while (!currentNode.state().isTerminal()) {
                currentNode = mcts.runMCTS(1000);
                if (i == 0 && detailedPrint) {
                    System.out.println("Current Board State:");
                    System.out.println(currentNode.state());
                }
                mcts = new MCTS(currentNode);
            }

            Optional<Integer> winner = currentNode.state().winner();
            if (winner.isPresent() && winner.get() == 1) {
                winCountAI++;
            }

            if (i == 0 && detailedPrint) {
                System.out.println("----- Running additional games to calculate win probability -----");
                detailedPrint = false;
            }

            // Random play games
            game = new TicTacToe();
            currentNode = new TicTacToeNode(game.start(), null);
            while (!currentNode.state().isTerminal()) {
                List<Move<TicTacToe>> moves = new ArrayList<>(currentNode.state().moves(currentNode.state().player()));
                Move<TicTacToe> randomMove = moves.get(new Random().nextInt(moves.size()));
                currentNode = new TicTacToeNode(currentNode.state().next(randomMove), currentNode);
            }

            winner = currentNode.state().winner();
            if (winner.isPresent() && winner.get() == 1) {
                winCountRandom++;
            }
        }

        double winProbabilityAI = (double) winCountAI / numberOfGames;
        double winProbabilityRandom = (double) winCountRandom / numberOfGames;
        System.out.println("AI (Player X) with MCTS won " + winCountAI + " out of " + numberOfGames + " games. Win Probability: " + winProbabilityAI);
        System.out.println("Random (Player X) without MCTS won " + winCountRandom + " out of " + numberOfGames + " games. Win Probability: " + winProbabilityRandom);
    }
}
