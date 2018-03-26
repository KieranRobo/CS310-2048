package view;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {
	private static final long serialVersionUID = -8688748618155465054L;

	public ButtonPanel (JButton[] buttons) {
		setFocusable(false);
		setLayout(new GridLayout(1, buttons.length));
		for(JButton button : buttons) {
			add(button);
			button.setFocusable(false);
		}
	}
	
}
