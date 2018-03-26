package model;

import java.awt.Point;
import java.util.List;

import model.AbstractState.MOVE;

public interface State {
	
	/**
	 * No moves equals game over.
	 * @return A List of valid moves.  
	 */
	List<MOVE> getMoves();
	
	/**
	 * Get an array of half-move states.  Use the ordinal() method
	 * of the MOVE enum to index into the array.  If the move is not
	 * valid, the state for that move will be an unchanged copy.
	 * @return An array of states, one for each player move (UP, DOWN, LEFT, RIGHT)
	 */
	State[] nextFirstHalfMoveStates();
	
	/**
	 * Get a list of all possible states the adding of a random tile
	 * may generate.  This should only be called after making a first
	 * half-move.
	 * @return A List of State objects, one for each possible random tile
	 */
	List<State> nextSecondHalfMoveStates();
	
	/**
	 * Reset the state.
	 */
	void reset();
	
	/**
	 * Current score.
	 * @return int - the score of this state.
	 */
	int  getScore();
	
	/**
	 * Make a move.  This will change the state if the move is valid.
	 * This is a full move, so a random tile will be added after the
	 * chosen move.
	 * @param dir - The chosen move.
	 */
	void move(MOVE dir);
	
	/**
	 * Get the value of the tile at (x,y)
	 * @param x - the x coordinate of the tile.
	 * @param y - the y coordinate of the tile.
	 * @return The value of the tile at coordinate (x,y)
	 */
	int  getValue(int x, int y);
	
	/**
	 * Store all the board data in a single long (64 bits)
	 * The score is not included, only the tile values.
	 * @return the board data in a long
	 */
	long toLong();
	
	/**
	 * Restore the board data from a long
	 * @param data the board data as a long
	 */
	void fromLong(long data);
	
	/**
	 * Store all the board data, including the score
	 * @return StateData object which includes board data and current score
	 */
	StateData toStateData();
	
	/**
	 * Restore the State from a StateData object.
	 * @param stateData a StateData object.
	 */
	void fromStateData(StateData stateData);
	
	/**
	 * Undo the previous move
	 */
	void undo();
	
	/**
	 * Redo the previous undo, if any.
	 */
	void redo();
	
	/**
	 * This method is used to test if a move is possible.  If the
	 * result of a move is that the states are equal, that move is
	 * not legal.  As such, it doesn't need to compare scores.
	 * @param that: A child of 'this::State'
	 * @return true if the board hasn't changed, false otherwise.
	 */
	boolean equals(State that);
	
	/**
	 * Get the highest tile value
	 * @return the highest tile value
	 */
	int getHighestTileValue();
	
	/**
	 * Get the position of the randomly generated tile.  It is used by
	 * the view to make the new tile red, but may have other uses.
	 * @return The position of the new tile
	 */
	Point getNewTilePosition();
	
	/**
	 * This method only makes the player's half of the move.
	 * Use with caution! This does not make a complete move,
	 * so you must undo it if you want to continue.
	 * @param dir - The player's move (UP,DOWN,LEFT,RIGHT)
	 * @return how much that move scored
	 */
	int halfMove(MOVE dir);
	
	/**
	 * @param dir - The potential move (UP,DOWN,LEFT,RIGHT)
	 * @return how much that move would score
	 */
	int moveScore(MOVE dir);
	
	/**
	 * @return A new State identical to this one
	 */
	State copy();
	
	/**
	 * Used as a time trial version of 2048 to force a random tile to be placed without taking a move.
	 * This is used in the cases where the user takes too long to make a move.
	 */
	void timeTrialMove();
	
	int getNumberOfEmptyCells();

	int[][] getBoardArray();

	void setVal(int x, int y, int i);

	void updateTime(int time);
}
