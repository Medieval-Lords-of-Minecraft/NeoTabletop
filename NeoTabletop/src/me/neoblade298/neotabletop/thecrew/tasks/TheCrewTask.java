package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.HashMap;

import me.neoblade298.neotabletop.thecrew.TheCrewInstance;

public abstract class TheCrewTask {
	protected HashMap<Integer, Integer> difficulty = new HashMap<Integer, Integer>();
	public void onTrick(TheCrewInstance inst) {}
	public void onLastTrick(TheCrewInstance inst) {}
}
