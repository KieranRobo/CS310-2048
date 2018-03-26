package ai;

import javax.swing.JPanel;

import model.AbstractState.MOVE;
import model.State;

public interface Player {
	MOVE getMove(State game);
	JPanel getPlayerPanel();
	void reset();
	int studentID();
	String studentName();
}
