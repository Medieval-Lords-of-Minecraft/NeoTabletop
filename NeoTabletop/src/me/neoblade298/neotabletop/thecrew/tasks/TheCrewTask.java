package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.HashMap;

import me.neoblade298.neotabletop.thecrew.TheCrewInstance;

public abstract class TheCrewTask {
	protected HashMap<Integer, Integer> difficulty = new HashMap<Integer, Integer>();
	protected String display;
	protected boolean isComplete;
	
	public void onTrick(TheCrewInstance inst) {}
	public void onLastTrick(TheCrewInstance inst) {}
	public int getDifficulty(int players) {
		return difficulty.get(players);
	}
	public String getDisplay() {
		return display;
	}
	public boolean isComplete() {
		return isComplete;
	}
}
