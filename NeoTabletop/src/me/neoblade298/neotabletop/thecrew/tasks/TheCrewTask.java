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
	protected boolean isComplete;
	
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

	public abstract TheCrewTask clone(TheCrewPlayer owner);
	public abstract TaskResult onTrickEnd(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile); // Return false to end game
	public abstract TaskResult afterLastTrick(TheCrewInstance inst); // Return false to end game
	
	public enum TaskResult {
		FAIL,
		NONE,
		SUCCESS;
	}
}
