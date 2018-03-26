package ai;

import java.util.Map;
import java.util.Random;

import model.AbstractState.MOVE;
import model.State;


public class KieranRobertson2048 extends AbstractPlayer {

    private Random rng = new Random();

    private MOVE recursiveBestMove(State game, int depth) {

        if (depth == 1) {
            return bestSingleMove(game);
        }

        int bestScore = 0;
        MOVE bestMove = null;

        for (MOVE move : game.getMoves()) {
            State gameCopy = game.copy();

            int currentScore = 0;

            currentScore += gameCopy.moveScore(move);
            gameCopy.move(move);

            if (currentScore >= bestScore) {
                bestScore = currentScore;
                bestMove = move;

            }

        }
        System.out.println(bestMove.toString());

        State gameCopy = game.copy();
        gameCopy.move(bestMove);
        return recursiveBestMove(gameCopy, depth-1);
    }


    public MOVE getMove(State game) {
        pause();


        return recursiveBestMove(game, 2);
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

/*
public class KieranRobertson2048 extends AbstractPlayer {

    private class moveDetails {
        int score;
        MOVE move;

        public moveDetails(MOVE move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    private Random rng = new Random();


    State gameCopy;


    @Override
    public MOVE getMove(State game) {
        pause();

        gameCopy = game;

        State gameCopy;

        int highScore = 0;
        MOVE bestMove = null;

        for (MOVE move : game.getMoves()) {
            gameCopy = game;

            int tempScore = gameCopy.moveScore(move);
            gameCopy.move(move);

            tempScore += gameCopy.moveScore(highestScoreMove(gameCopy));

            if (tempScore > highScore) {
                highScore = tempScore;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private MOVE highestScoreMove(State game) {
        int bestMoveScore = 0;
        MOVE bestMove = null;

        for (MOVE move : game.getMoves()) {
            if (game.moveScore(move) > bestMoveScore) {
                bestMoveScore = game.moveScore(move);
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Player 0 is AI, Player 1 is random placer
    // Returns move and score that said move produces
    private moveDetails minimax(int depth, int player) {
        int bestScore;
        int currentScore;

        if (depth == 0) {

        }

        if (player == 0)
            bestScore = Integer.MIN_VALUE;
        else
            bestScore = Integer.MAX_VALUE;

        MOVE bestMove = null;

        for (MOVE move : gameCopy.getMoves()) {
            if (player == 0) { // AI
                currentScore = minimax(depth-1, 1).score;
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestMove = move;
                }
            }
            else // RANDOMER
            {
                currentScore = minimax(depth-1, 1).score;
                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    bestMove = move;
                }
            }
        }
        return new moveDetails(bestMove, bestScore);
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
*/