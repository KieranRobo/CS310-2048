package view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.CircularArray;

public class TimePanel extends JPanel {
	private static final long serialVersionUID = -8262752035381756841L;
	private final CircularArray ten = new CircularArray(10);
	private final CircularArray ton = new CircularArray(100);
	private int total = 0;
	private int count = 0;
	private final JLabel[] labels;
	
	public TimePanel() {
		setFocusable(false);
		setLayout(new GridLayout(1, 4));
		setAlignmentX(CENTER_ALIGNMENT);
		labels = new JLabel[4];
		for(int i = 0 ; i < 4 ; i++) {
			labels[i] = new JLabel("0");
			add(labels[i],i);
		}
	}
	
	public void reset() {
		total = 0;
		count = 0;
		ten.clear();
		ton.clear();
		for(int i = 0 ; i < 4 ; i++) {
			labels[i].setText("0");
			labels[i].setAlignmentX(JLabel.CENTER_ALIGNMENT);
		}
	}
	
	public void updateTime(int time) {
		ten.add(time);
		ton.add(time);
		total += time;
		count++;
		labels[0].setText(""+time);
		labels[1].setText(""+ten.average());
		labels[2].setText(""+ton.average());
		labels[3].setText(""+(total/count));
	}
	
}
