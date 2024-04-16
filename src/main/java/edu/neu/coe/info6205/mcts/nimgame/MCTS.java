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
            List<Move<NimGame>> possibleMoves = (List<Move<NimGame>>) currentNode.state().moves(currentNode.state().player());
            if (!possibleMoves.isEmpty()) {
                Move<NimGame> selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
                currentNode = new NimGameNode(currentNode.state().next(selectedMove), currentNode.getParent());
            }
        }
        return currentNode.state().winner().orElse(-1); // 返回赢家的标识
    }


    private void makeStrategicMove(State<NimGame> state, Node<NimGame> currentNode) {
        int[] piles = ((NimGameState)state).getPiles();
        int nonZeroCount = (int) Arrays.stream(piles).filter(p -> p > 0).count();

        if (nonZeroCount == 1) {
            for (int i = 0; i < piles.length; i++) {
                if (piles[i] > 1) { // 找到唯一的非零堆并留下一个棋子
                    State<NimGame> newState = state.next(new NimGameMove(i, piles[i] - 1));
                    Node<NimGame> newNode = new NimGameNode(newState, currentNode);
                    currentNode.children().add(newNode);
                    return;
                }
            }
        }

        // 在所有非终结状态下找到一个策略性移动
        for (int i = 0; i < piles.length; i++) {
            if (piles[i] > 1) {
                State<NimGame> newState = state.next(new NimGameMove(i, piles[i] - 1));  // 尝试只留一个棋子
                if (newState.isTerminal()) {
                    Node<NimGame> newNode = new NimGameNode(newState, currentNode);
                    currentNode.children().add(newNode);
                    return;  // 如果这个移动导致游戏结束，就执行它
                }
            }
        }

        // 默认策略：随机移动
        List<Move<NimGame>> moves = (List<Move<NimGame>>) state.moves(state.player());
        if (!moves.isEmpty()) {
            Move<NimGame> move = moves.get(random.nextInt(moves.size()));
            State<NimGame> newState = state.next(move);
            Node<NimGame> newNode = new NimGameNode(newState, currentNode);
            currentNode.children().add(newNode);
        }
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


    public static void main(String[] args) {
        int totalGames = 5000;
        boolean detailedDisplay = true; // 控制是否显示详细的每一步棋

        // MCTS AI对弈部分
        int mctsWins = 0; // 统计MCTS AI胜利的次数
        for (int gameCount = 0; gameCount < totalGames; gameCount++) {
            NimGame game = new NimGame();
            Node<NimGame> rootNode = new NimGameNode(game.start(), null);
            boolean isMctsTurn = true; // MCTS AI先手

            while (!rootNode.state().isTerminal()) {
                if (isMctsTurn) {
                    rootNode = new MCTS(rootNode).runMCTS(2000); // MCTS AI进行决策
                    if (detailedDisplay) {
                        System.out.println("MCTS AI made a move:");
                        printPiles(((NimGameState)rootNode.state()).getPiles());
                    }
                } else {
                    rootNode = simpleAiMove(rootNode); // 简单AI进行随机决策
                    if (detailedDisplay) {
                        System.out.println("Simple AI made a move:");
                        printPiles(((NimGameState)rootNode.state()).getPiles());
                    }
                }
                isMctsTurn = !isMctsTurn; // 轮换玩家
            }

            if (!isMctsTurn) { // 如果是MCTS AI最后一个移动，则MCTS AI输
                // 这里不做操作
            } else {
                mctsWins++;  // 如果不是 MCTS AI 最后一个动作，即它赢了比赛
            }

            detailedDisplay = false; // 只在第一局显示详细信息
        }

        System.out.println("MCTS AI won " + mctsWins + " out of " + totalGames + " games.");
        System.out.println("Win Probability: " + ((double) mctsWins / totalGames));

        // 运行两个普通AI对弈1000次，查看先手的胜率
        runSimpleAIGames(totalGames);
    }

    // 运行两个简单AI对弈的方法
    private static void runSimpleAIGames(int totalGames) {
        int firstPlayerWins = 0; // 统计先手AI胜利的次数
        for (int gameCount = 0; gameCount < totalGames; gameCount++) {
            Node<NimGame> rootNode = new NimGameNode(new NimGame().start(), null);
            boolean isTurnOfFirstPlayer = true; // 先手AI始终先手

            while (!rootNode.state().isTerminal()) {
                rootNode = simpleAiMove(rootNode); // 每个AI轮流移动
                isTurnOfFirstPlayer = !isTurnOfFirstPlayer; // 轮换玩家
            }

            if (!isTurnOfFirstPlayer) { // 如果游戏结束时轮到后手，那么先手是最后一个移动的
                firstPlayerWins++;
            }
        }

        System.out.println("First Player (Simple AI) won " + firstPlayerWins + " out of " + totalGames + " games.");
        System.out.println("Win Probability for First Player (Simple AI): " + ((double) firstPlayerWins / totalGames));
    }

    // 简单AI随机选择一个合法移动的方法
    private static Node<NimGame> simpleAiMove(Node<NimGame> node) {
        List<Move<NimGame>> moves = (List<Move<NimGame>>) node.state().moves(node.state().player());
        if (moves.isEmpty()) {
            return node; // 如果没有可行的移动，直接返回当前节点
        }
        Move<NimGame> move = moves.get(new Random().nextInt(moves.size())); // 随机选择一个移动
        return new NimGameNode(node.state().next(move), node);
    }

    // 打印当前棋子堆的状态
    private static void printPiles(int[] piles) {
        System.out.println("Current state:");
        for (int i = 0; i < piles.length; i++) {
            System.out.println("Pile " + (i + 1) + ": " + piles[i]);
        }
    }

}