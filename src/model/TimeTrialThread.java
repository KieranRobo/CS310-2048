package model;

import view.FancyPanel;

public class TimeTrialThread implements Runnable {

	private State s;
	private FancyPanel panel = null;
	private boolean running = true;

	public TimeTrialThread(State s, FancyPanel panel) {
		this.s = s;
		this.panel = panel;
	}

	public TimeTrialThread(State s) {
		this.s = s;
	}

	public void pause() {
		running = false;
	}

	public void resume() {
		running = true;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		while (s.getMoves().size() > 0 && running) {
			end = System.currentTimeMillis();
			if (end - start >= 1000 && running == true) {
				s.timeTrialMove();
				if (panel != null) {
					panel.repaint();
				}
				start = System.currentTimeMillis();
			}
		}
		System.out.println("Thread paused");

	}
}