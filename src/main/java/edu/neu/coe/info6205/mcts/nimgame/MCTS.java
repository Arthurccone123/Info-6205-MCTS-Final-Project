package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Game;
import edu.neu.coe.info6205.mcts.core.Move;
import edu.neu.coe.info6205.mcts.core.Node;
import edu.neu.coe.info6205.mcts.core.State;

import java.util.*;
import java.util.stream.Collectors;

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


    private double uctValue(Node<NimGame> node, int totalPlayouts) {
        if (node.playouts() == 0) return Double.MAX_VALUE;
        double winRate = node.wins() / (double) node.playouts();
        double logTerm = Math.log(totalPlayouts) / node.playouts();
        return winRate + EXPLORATION_CONSTANT * Math.sqrt(logTerm);
    }

    private Node<NimGame> expand(Node<NimGame> node) {
        makeStrategicMove(node.state(), node);
        List<Node<NimGame>> childrenList = new ArrayList<>(node.children());
        if (!childrenList.isEmpty()) {
            // If a strategic move was made
            return childrenList.get(childrenList.size() - 1);
        }

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
            List<Move<NimGame>> possibleMoves = heuristicMoveSelection(currentNode.state());
            if (possibleMoves.isEmpty()) {
                possibleMoves = (List<Move<NimGame>>) currentNode.state().moves(currentNode.state().player());
            }
            Move<NimGame> selectedMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            currentNode = new NimGameNode(currentNode.state().next(selectedMove), currentNode.getParent());
        }
        return currentNode.state().winner().orElse(-1);
    }



    private void makeStrategicMove(State<NimGame> state, Node<NimGame> currentNode) {
        int[] piles = ((NimGameState)state).getPiles();
        if (piles[0] == 0 && piles[1] == piles[2] && piles[1] > 0) {

            State<NimGame> newState = state.next(new NimGameMove(1, piles[1]));
            Node<NimGame> newNode = new NimGameNode(newState, currentNode);
            currentNode.children().add(newNode);
        } else {
            List<Move<NimGame>> bestMoves = findStrategicMoves(piles);
            if (!bestMoves.isEmpty()) {
                Collections.shuffle(bestMoves);
                Move<NimGame> move = bestMoves.get(0);
                State<NimGame> newState = state.next(move);
                Node<NimGame> newNode = new NimGameNode(newState, currentNode);
                currentNode.children().add(newNode);
            }
        }
    }

    private List<Move<NimGame>> findStrategicMoves(int[] piles) {
        List<Move<NimGame>> bestMoves = new ArrayList<>();
        for (int i = 0; i < piles.length; i++) {
            for (int numToRemove = 1; numToRemove <= piles[i]; numToRemove++) {
                int[] newPiles = piles.clone();
                newPiles[i] -= numToRemove;
                if (isPotentiallyWinningMove(newPiles)) {
                    bestMoves.add(new NimGameMove(i, numToRemove));
                }
            }
        }
        return bestMoves;
    }

    private boolean isPotentiallyWinningMove(int[] newPiles) {
        int zeroCount = (int) Arrays.stream(newPiles).filter(p -> p == 0).count();
        if (zeroCount != 1) {
            return false;
        }
        int[] nonZeros = Arrays.stream(newPiles).filter(p -> p > 0).toArray();
        return nonZeros.length == 2 && nonZeros[0] == nonZeros[1] && nonZeros[0] > 1;
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

    private List<Move<NimGame>> heuristicMoveSelection(State<NimGame> state) {
        int[] piles = ((NimGameState)state).getPiles();
        List<Move<NimGame>> possibleMoves = new ArrayList<>();

        for (int i = 0; i < piles.length; i++) {
            for (int numToRemove = 1; numToRemove <= piles[i]; numToRemove++) {
                int[] newPiles = piles.clone();
                newPiles[i] -= numToRemove;

                if (isDesirableState(newPiles)) {
                    possibleMoves.add(new NimGameMove(i, numToRemove));
                }
            }
        }

        return possibleMoves.isEmpty() ? (List<Move<NimGame>>) state.moves(state.player()) : possibleMoves;
    }

    private boolean isDesirableState(int[] piles) {

        int countNonZero = (int) Arrays.stream(piles).filter(p -> p > 0).count();
        if (countNonZero == 1) {

            return Arrays.stream(piles).anyMatch(p -> p == 1);
        }

        Set<Integer> uniquePileSizes = Arrays.stream(piles).filter(p -> p > 0).boxed().collect(Collectors.toSet());
        return uniquePileSizes.size() > 1;
    }


    public static void main(String[] args) {
        NimGame game = new NimGame();
        Node<NimGame> rootNode = new NimGameNode(game.start(), null);
        MCTS mcts = new MCTS(rootNode);

        int currentPlayer = game.opener();
        Scanner scanner = new Scanner(System.in);

        while (!game.isGameOver()) {
            System.out.println("Current state:");
            printPiles(game.getCurrentState().getPiles());

            if (currentPlayer == game.opener()) {
                Node<NimGame> bestMoveNode = mcts.runMCTS(100000);
                game.setCurrentState((NimGameState) bestMoveNode.state());
                System.out.println("AI (Player " + currentPlayer + ") made a move.");
            } else {
                System.out.print("Enter the pile index to remove from (1-3): ");
                int pileIndex = scanner.nextInt() - 1;
                System.out.print("Enter the number of pieces to remove: ");
                int pieces = scanner.nextInt();
                try {
                    game.playMove(new NimGameMove(pileIndex, pieces));
                    System.out.println("Human player (Player " + currentPlayer + ") made a move.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid move, please try again.");
                    continue;
                }
            }

            currentPlayer = 1 - currentPlayer;

            if (game.isGameOver()) {
                System.out.println("Game Over! The last player to move loses.");
            }

            mcts = new MCTS(new NimGameNode(game.getCurrentState(), null));
        }
        scanner.close();
    }

    private static void printPiles(int[] piles) {
        for (int i = 0; i < piles.length; i++) {
            System.out.println("Pile " + (i + 1) + ": " + piles[i]);
        }
    }
}