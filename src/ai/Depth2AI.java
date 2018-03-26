package ai;

import java.util.List;
import java.util.Random;

import model.AbstractState.MOVE;
import model.State;

public class Depth2AI extends AbstractPlayer {

    private Random rng = new Random();

    @Override
    public MOVE getMove(State game) {
        pause();

        int bestScore = 0;
        MOVE bestMove = null;

        for (MOVE move : game.getMoves()) {
            State gameCopy = game.copy();
            int currentScore = 0;

            currentScore += gameCopy.moveScore(move);
            gameCopy.move(move);


            for (MOVE move2 : gameCopy.getMoves()) {
                State gameCopy2 = gameCopy.copy();
                int currentScore2 = 0;

                currentScore2 += gameCopy2.moveScore(move2);
                gameCopy2.move(move2);


                MOVE nextMove2 = bestSingleMove(gameCopy2);
                currentScore2 += gameCopy2.moveScore(nextMove2);
                if (currentScore2 >= bestScore) {
                    bestScore = currentScore2;
                    bestMove = move;
                }
            }

        }

        return bestMove;
    }

    private MOVE bestSingleMove(State game) {
        int bestMoveScore = 0;
        MOVE bestMove = null;

        for (MOVE move : game.getMoves()) {
            if (game.moveScore(move) >= bestMoveScore) {
                bestMoveScore = game.moveScore(move);
                bestMove = move;
            }
        }

        return bestMove;
    }

    @Override
    public int studentID() {
        return 201547266;
    }

    @Override
    public String studentName() {
        return "Kieran Robertson";
    }
}
