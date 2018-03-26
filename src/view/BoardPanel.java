package view;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import model.State;

public class BoardPanel extends JPanel {
	
	private static final long serialVersionUID = -3256290564832372809L;
	private final State board;
	
	public BoardPanel(State board) {
		this.board = board;
		setFont(new Font("courier", 0, 40));
	}
	
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		int unit = getWidth() / 8;
		for(int x = 0 ; x < 4 ; x++) {
			g.drawLine(x * unit * 2, 0, x * unit * 2, getHeight());
			for(int y = 0 ; y < 4 ; y++) {
				int value = board.getValue(x,y);
				String s = value == 0 ? "" : Integer.toString(value);
				int w = g.getFontMetrics().stringWidth(s);
				g.drawString(s, ((x * 2 + 1) * unit) - (w/2), (y * 2 + 1) * unit);
			}	
		}
		for(int y = 0 ; y < 4 ; y++) {
			g.drawLine(0, y * unit * 2, getWidth(), y * unit * 2);
		}
		g.dispose();
	}
}
