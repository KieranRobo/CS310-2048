package ai;

import java.util.List;
import java.util.Random;

import model.AbstractState.MOVE;
import model.State;

public class RandomAI extends AbstractPlayer {

	private Random rng = new Random();

	@Override
	public MOVE getMove(State game) {
		// Delay for the view
		pause();
		// Get available moves
		List<MOVE> moves = game.getMoves();
		// Pick a move at random
		return moves.get(rng.nextInt(moves.size()));
	}

	@Override
	public int studentID() {
		return 201181111;
	}

	@Override
	public String studentName() {
		return "Phil Rodgers";
	}
}
