package ai;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;

import view.ComboObserver;
import view.ComboPanel;

public abstract class AbstractPlayer implements Player, ComboObserver {

	protected int delay = 0;
	protected JPanel panel = new BasicDelayPanel();

	@Override
	public void comboChanged(String id, int index) {
		switch (id) {
		case "Delay (ms)":
			delay = 1 << index;
			break;
		default:
		}
	}

	private class BasicDelayPanel extends JPanel {
		private static final long serialVersionUID = 4875042479359355572L;

		public BasicDelayPanel() {
			setFocusable(false);
			setLayout(new BorderLayout());
			Vector<String> names = new Vector<String>();
			names = new Vector<String>();
			for (int i = 0; i < 20; i++) {
				names.add("" + (1 << i));
			}
			add(new ComboPanel("Delay (ms)", names, AbstractPlayer.this), BorderLayout.CENTER);
		}
	}

	protected void pause() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public JPanel getPlayerPanel() {
		return panel;
	}

}
