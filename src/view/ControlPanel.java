package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ai.Player;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 4231304967321746399L;
	private JComboBox<String> combo;
	public Player player;
	private JFrame cFrame;
	private final TimePanel tp = new TimePanel();

	public ControlPanel(final List<Player> players, JButton... buttons) {
		setFocusable(false);
		Vector<String> names = new Vector<String>();
		player = players.get(0);
		for (Player e : players) {
			names.add(e.getClass().getSimpleName());
		}
		combo = new JComboBox<String>(names);
		combo.setFocusable(false);
		combo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JComboBox<?>) {
					JComboBox<?> jcb = (JComboBox<?>) e.getSource();
					player = players.get(jcb.getSelectedIndex());
					remove(1);
					add(player.getPlayerPanel(), 1);
					if (cFrame != null) {
						cFrame.getContentPane().revalidate();
						cFrame.getContentPane().repaint();
					}
				}
			}
		});
		setLayout(new GridLayout(4, 1));
		add(combo, 0);
		add(player.getPlayerPanel(), 1);
		add(tp, 2);
		add(new ButtonPanel(buttons), 3);
		setFocusable(false);
	}

	public void updateTime(int time) {
		tp.updateTime(time);
		repaint();
	}

	public void setFrame(JFrame cFrame) {
		this.cFrame = cFrame;
	}

	public void reset() {
		tp.reset();
	}
}
