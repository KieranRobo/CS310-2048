package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboPanel extends JPanel {
	private static final long serialVersionUID = -6339420676722359038L;
	private final String id;
	private final ComboObserver observer;
	public ComboPanel(String id, Vector<String> names, ComboObserver obsever) {
		setFocusable(false);
		this.id = id;
		this.observer = obsever;
		add(new JLabel(id), 0);
		JComboBox<String> combo = new JComboBox<String>(names);
		add(combo, 1);
		combo.setFocusable(false);
		combo.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() instanceof JComboBox<?>) {
					JComboBox<?> jcb = (JComboBox<?>)e.getSource();
					ComboPanel.this.observer.comboChanged(
							ComboPanel.this.id, jcb.getSelectedIndex());
				}
			}
		});
	}
}
