package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Game;
import edu.neu.coe.info6205.mcts.core.State;
import edu.neu.coe.info6205.mcts.nimgame.NimGameMove;
import edu.neu.coe.info6205.mcts.nimgame.NimGameState;



import java.util.Scanner;

public class NimGame implements Game<NimGame> {
    private NimGameState currentState;

    public NimGame() {
        currentState = new NimGameState(new int[]{3, 6, 9});
    }

    @Override
    public State<NimGame> start() {
        return (State<NimGame>) currentState;
    }

    @Override
    public int opener() {
        return 1;
    }

    public void playMove(NimGameMove move) throws IllegalArgumentException {
        int[] piles = currentState.getPiles();
        if (move.getNumberOfPieces() <= 0) {
            throw new IllegalArgumentException("You must remove at least one piece.");
        }
        if (piles[move.getPileIndex()] >= move.getNumberOfPieces()) {
            currentState = (NimGameState) currentState.next(move);
        } else {
            throw new IllegalArgumentException("Cannot remove more pieces than are present in the pile.");
        }
    }


    public boolean isGameOver() {
        return currentState.isTerminal();
    }

    public NimGameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(NimGameState state) {
        currentState = state;
    }

    public static void main(String[] args) {
        NimGame game = new NimGame();
        Scanner scanner = new Scanner(System.in);

        while (!game.isGameOver()) {
            System.out.println("Current pile state:");
            int[] piles = game.getCurrentState().getPiles();
            for (int i = 0; i < piles.length; i++) {
                System.out.println("Pile " + (i + 1) + ": " + piles[i]);
            }

            System.out.print("Choose a pile to move (1-3): ");
            int pileIndex = scanner.nextInt() - 1;
            System.out.print("From pile " + (pileIndex + 1) + " remove how many: ");
            int pieces = scanner.nextInt();

            try {
                game.playMove(new NimGameMove(pileIndex, pieces));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            if (game.isGameOver()) {
                System.out.println("Game Over, the last move player is lost!");
                break;
            }

        }
        scanner.close();
    }

}
