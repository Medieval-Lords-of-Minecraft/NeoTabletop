package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public abstract class TheCrewTask {
	protected TheCrewPlayer owner;
	protected HashMap<Integer, Integer> difficulty = new HashMap<Integer, Integer>();
	protected String display;
	protected boolean isComplete = false;
	
	public TheCrewTask(TheCrewPlayer owner, TheCrewTask src, TheCrewInstance inst) {
		this.owner = owner;
		this.display = src.display;
		this.difficulty = src.difficulty;
	}
	
	public TheCrewTask(Configuration sec) {
		Configuration diff = sec.getSection("difficulty");
		for (String key : diff.getKeys()) {
			int ikey = Integer.parseInt(key);
			difficulty.put(ikey, diff.getInt(key));
		}
	}
	
	public HashMap<Integer, Integer> getDifficulty() {
		return difficulty;
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
	public abstract void showDebug(CommandSender s);
	public abstract TheCrewTask clone(TheCrewPlayer owner, TheCrewInstance inst);
	public abstract boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile);
	public abstract boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile);
	
	public enum TaskResult {
		FAIL,
		NONE,
		SUCCESS;
	}
}
