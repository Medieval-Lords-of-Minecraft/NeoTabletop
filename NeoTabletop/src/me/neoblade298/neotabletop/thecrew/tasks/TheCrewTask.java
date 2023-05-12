package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;

public abstract class TheCrewTask {
	protected TheCrewPlayer owner;
	protected HashMap<Integer, Integer> difficulty = new HashMap<Integer, Integer>();
	protected String display;
	protected boolean isComplete = false;
	
	public TheCrewTask(TheCrewPlayer owner) {
		this.owner = owner;
	}
	
	public int getDifficulty(int players) {
		return difficulty.get(players);
	}
	public String getDisplay() {
		return display;
	}
	public boolean isComplete() {
		return isComplete;
	}
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public abstract void reset();
	public abstract TheCrewTask clone(TheCrewPlayer owner);
	public abstract boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick);
	public abstract boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick);
	
	public enum TaskResult {
		FAIL,
		NONE,
		SUCCESS;
	}
}
