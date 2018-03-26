package model;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class AbstractState implements State {

	public static final int N_START_TILES = 2;
	protected int score = 0;
	protected final Stack<StateData> undoStack = new Stack<StateData>();
	protected final Stack<StateData> redoStack = new Stack<StateData>();
	protected Random rng = new Random();
	protected Point newTilePosition = new Point(-1, -1);

	protected int moves = 0;
	protected int time = 0;

	public static enum MOVE {
		LEFT, UP, RIGHT, DOWN;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void reset() {
		score = 0;
		moves = 0;
		time = 0;
		undoStack.clear();
		redoStack.clear();
	}

	@Override
	public final void undo() {
		if (undoStack.isEmpty())
			return;
		redoStack.push(toStateData());
		fromStateData(undoStack.pop());
	}

	@Override
	public final void redo() {
		if (redoStack.isEmpty())
			return;
		undoStack.push(toStateData());
		fromStateData(redoStack.pop());
	}

	@Override
	public long toLong() {
		long data = 0L;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				data |= getValue(x, y) & 0xF;
				if (y == 3 && x == 3) {
					return data;
				}
				data <<= 4;
			}
		}
		// This can never happen, but the compiler doesn't know that.
		return data;
	}

	@Override
	public State[] nextFirstHalfMoveStates() {
		State[] children = new State[4];
		for (MOVE move : MOVE.values()) {
			move(move);
			children[move.ordinal()] = new BinaryState(toLong(), score);
			if (getMoves().contains(move)) {
				undo();
			}

		}
		return children;
	}

	@Override
	public StateData toStateData() {
		return new StateData(toLong(), score);
	}

	@Override
	public boolean equals(State that) {
		return this.toLong() == that.toLong();
	}

	protected int log2(int n) {
		if (n == 0)
			return 0;
		return (int) (Math.log(n) / Math.log(2));
	}

	public int getHighestTileValue() {
		int highest = -1;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				highest = Math.max(highest, getValue(x, y));
			}
		}
		return highest;
	}

	@Override
	public Point getNewTilePosition() {
		return newTilePosition;
	}

	@Override
	public void updateTime(int time) {
		moves++;
		this.time += time;
	}

	public int getAvgTime() {
		if (moves == 0)
			return time;
		return time / moves;
	}
}
