package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.JPanel;

import model.State;

public class FancyPanel extends JPanel {
	private static final long serialVersionUID = 3293890758507489460L;

	private final State board;

	public FancyPanel(State board) {
		this.board = board;
		setBackground(new Color(0xff9933));
		try {
			GraphicsEnvironment ge = 
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font f = Font.createFont(Font.TRUETYPE_FONT, new File("res/caviar_dreams/CaviarDreams_Bold.ttf"));
			System.out.println(f.getName());
			System.out.println(ge.registerFont(f));
			setFont(new Font(f.getName(), 0, 40));
		} catch (Exception ioe) {
		}
		requestFocusInWindow();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		g.clearRect(0, 0, getWidth(), getHeight());
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		int unit = getWidth() / 8;
		for(int x = 0 ; x < 4 ; x++) {
			for(int y = 0 ; y < 4 ; y++) {
				int value = board.getValue(x,y);
				if(value == 0) {
					g2.setColor(new Color(0xcdc1b4));
					g2.fillRoundRect(x * unit * 2 + 5, y * unit * 2 + 5, unit * 2 - 10, unit * 2 - 10, unit / 4, unit / 4);
				} else {
					g2.setColor(new Color(0xeee4da));
					g2.fillRoundRect(x * unit * 2 + 5, y * unit * 2 + 5, unit * 2 - 10, unit * 2 - 10, unit / 4, unit / 4);
					String s = value == 0 ? "" : Integer.toString(value);
					int w = g.getFontMetrics().stringWidth(s);
					g2.setColor(new Color(0x776e65));
					if(board.getNewTilePosition().equals(new Point(x,y))) {
						g2.setColor(Color.RED);
					}
					g.drawString(s, ((x * 2 + 1) * unit) - (w/2), (y * 2 + 1) * unit + 10);
				}
			}	
		}
		g.dispose();
	}
}
