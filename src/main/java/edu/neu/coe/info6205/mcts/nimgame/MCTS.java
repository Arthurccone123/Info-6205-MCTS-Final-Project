package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;

import java.util.*;

public class MCTS {
    private final Node<NimGame> root;
    private final static double EXPLORATION_CONSTANT = Math.sqrt(2);
    private final Random random = new Random();

    public MCTS(Node<NimGame> root) {
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

    double uctValue(Node<NimGame> node, int totalPlayouts) {
        double baseExploration = EXPLORATION_CONSTANT;
        double dynamicFactor = Math.log((1 + node.playouts() + baseExploration) / baseExploration);
        double winRate = (double) node.wins() / node.playouts();
        double explorationValue = Math.sqrt(dynamicFactor * Math.log(totalPlayouts) / node.playouts());
        return winRate + explorationValue;
    }

    Node<NimGame> expand(Node<NimGame> node) {
        makeStrategicMove(node.state(), node);
        Collection<Node<NimGame>> children = node.children();
        if (!children.isEmpty()) {
            List<Node<NimGame>> childrenList = new ArrayList<>(children);
            return childrenList.get(childrenList.size() - 1);
        }

        // If no strategic move generates a new node, randomly select a move to expand
        List<Move<NimGame>> moves = new ArrayList<>(node.state().moves(node.state().player()));
        if (!moves.isEmpty()) {
            Move<NimGame> selectedMove = moves.get(random.nextInt(moves.size()));
            State<NimGame> newState = node.state().next(selectedMove);
            Node<NimGame> newNode = new NimGameNode(newState, node);
            node.children().add(newNode);
            return newNode;
        }
        return null;
    }

    private Move<NimGame> selectBestMove(Node<NimGame> node, List<Move<NimGame>> moves) {
        int bestScore = Integer.MAX_VALUE;
        Move<NimGame> bestMove = null;

        for (Move<NimGame> move : moves) {
            // Apply the move and get the new state
            State<NimGame> newState = node.state().next(move);

            int score = evaluateState((NimGameState)newState);

            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int evaluateState(NimGameState state) {
        int[] piles = state.getPiles();
        int xorSum = 0;

        for (int pile : piles) {
            xorSum ^= pile;
        }

        return xorSum;
    }

    int simulate(Node<NimGame> node) {
        Node<NimGame> currentNode = node;
        while (!currentNode.state().isTerminal()) {
            List<Move<NimGame>> possibleMoves = heuristicMoves(currentNode);
            Move<NimGame> selectedMove = possibleMoves.isEmpty() ? null : selectHeuristicMove(possibleMoves, currentNode);
            if (selectedMove != null) {
                currentNode = new NimGameNode(currentNode.state().next(selectedMove), currentNode.getParent());
            }
        }
        return currentNode.state().winner().orElse(-1);
    }

    private Move<NimGame> selectHeuristicMove(List<Move<NimGame>> moves, Node<NimGame> node) {
        // Prioritize moves that reduce the number of stones in the pile the most
        moves.sort((m1, m2) -> Integer.compare(((NimGameMove)m2).getPileReduction(), ((NimGameMove)m1).getPileReduction()));
        return moves.get(0);
    }

    private List<Move<NimGame>> heuristicMoves(Node<NimGame> node) {
        List<Move<NimGame>> moves = new ArrayList<>((List<Move<NimGame>>) node.state().moves(node.state().player()));
        Collections.shuffle(moves);
        return moves;
    }

    private void makeStrategicMove(State<NimGame> state, Node<NimGame> currentNode) {
        int[] piles = ((NimGameState)state).getPiles();
        int nonZeroCount = (int) Arrays.stream(piles).filter(p -> p > 0).count();

        if (nonZeroCount == 1) {
            for (int i = 0; i < piles.length; i++) {
                if (piles[i] > 1) { // Find the only non-zero pile and leave one stone
                    State<NimGame> newState = state.next(new NimGameMove(i, piles[i] - 1));
                    Node<NimGame> newNode = new NimGameNode(newState, currentNode);
                    currentNode.children().add(newNode);
                    return;
                }
            }
        }

        // In all non-terminal states, find a strategic move
        for (int i = 0; i < piles.length; i++) {
            if (piles[i] > 1) {
                State<NimGame> newState = state.next(new NimGameMove(i, piles[i] - 1));  // Attempt to leave only one stone
                if (newState.isTerminal()) {
                    Node<NimGame> newNode = new NimGameNode(newState, currentNode);
                    currentNode.children().add(newNode);
                    return;
                }
            }
        }

        // Random move
        List<Move<NimGame>> moves = (List<Move<NimGame>>) state.moves(state.player());
        if (!moves.isEmpty()) {
            Move<NimGame> move = moves.get(random.nextInt(moves.size()));
            State<NimGame> newState = state.next(move);
            Node<NimGame> newNode = new NimGameNode(newState, currentNode);
            currentNode.children().add(newNode);
        }
    }

    void backPropagate(Node<NimGame> node, int result) {
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

    public static void main(String[] args) {
        int totalGames = 1000;
        boolean detailedDisplay = true;

        int mctsWins = 0; // Count of MCTS AI victories
        for (int gameCount = 0; gameCount < totalGames; gameCount++) {
            NimGame game = new NimGame();
            Node<NimGame> rootNode = new NimGameNode(game.start(), null);
            boolean isMctsTurn = true;

            while (!rootNode.state().isTerminal()) {
                if (isMctsTurn) {
                    rootNode = new MCTS(rootNode).runMCTS(1000); // MCTS AI makes a decision
                    if (detailedDisplay) {
                        System.out.println("MCTS AI made a move:");
                        printPiles(((NimGameState)rootNode.state()).getPiles());
                    }
                } else {
                    rootNode = simpleAiMove(rootNode); // Simple AI makes a random decision
                    if (detailedDisplay) {
                        System.out.println("Simple AI made a move:");
                        printPiles(((NimGameState)rootNode.state()).getPiles());
                    }
                }
                isMctsTurn = !isMctsTurn; // Switch player
            }

            if (!isMctsTurn) {
            } else {
                mctsWins++;
            }

            detailedDisplay = false; // Display details only for the first game
        }

        System.out.println("MCTS AI won " + mctsWins + " out of " + totalGames + " games.");
        System.out.println("Win Probability: " + ((double) mctsWins / totalGames));

        runSimpleAIGames(totalGames);
    }

    // Run a game between two simple AIs
    private static void runSimpleAIGames(int totalGames) {
        int firstPlayerWins = 0; // Count of victories for the first player AI
        for (int gameCount = 0; gameCount < totalGames; gameCount++) {
            Node<NimGame> rootNode = new NimGameNode(new NimGame().start(), null);
            boolean isTurnOfFirstPlayer = true; // First player AI always starts

            while (!rootNode.state().isTerminal()) {
                rootNode = simpleAiMove(rootNode); // Each AI takes turns moving
                isTurnOfFirstPlayer = !isTurnOfFirstPlayer; // Switch player
            }

            if (!isTurnOfFirstPlayer) {
                firstPlayerWins++;
            }
        }

        System.out.println("First Player (Simple AI) won " + firstPlayerWins + " out of " + totalGames + " games.");
        System.out.println("Win Probability for First Player (Simple AI): " + ((double) firstPlayerWins / totalGames));
    }

    // Method for simple AI to randomly select a legal move
    private static Node<NimGame> simpleAiMove(Node<NimGame> node) {
        List<Move<NimGame>> moves = (List<Move<NimGame>>) node.state().moves(node.state().player());
        if (moves.isEmpty()) {
            return node;
        }
        Move<NimGame> move = moves.get(new Random().nextInt(moves.size()));
        return new NimGameNode(node.state().next(move), node);
    }


    private static void printPiles(int[] piles) {
        System.out.println("Current state:");
        for (int i = 0; i < piles.length; i++) {
            System.out.println("Pile " + (i + 1) + ": " + piles[i]);
        }
    }
}
