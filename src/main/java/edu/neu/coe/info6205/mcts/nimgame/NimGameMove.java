package edu.neu.coe.info6205.mcts.nimgame;

import edu.neu.coe.info6205.mcts.core.Move;

public class NimGameMove implements Move<NimGame> {
    private final int pileIndex;
    private final int numberOfPieces;  // 移除的石子数

    public NimGameMove(int pileIndex, int numberOfPieces) {
        this.pileIndex = pileIndex;
        this.numberOfPieces = numberOfPieces;
    }

    public int getPileIndex() {
        return pileIndex;
    }

    public int getNumberOfPieces() {
        return numberOfPieces;
    }

    // 获取这次移动减少的石子数
    public int getPileReduction() {
        return numberOfPieces;  // 直接返回移除的石子数
    }

    @Override
    public int player() {
        return 1;  // 假设这里总是返回1，需要根据你的游戏逻辑调整
    }
}
