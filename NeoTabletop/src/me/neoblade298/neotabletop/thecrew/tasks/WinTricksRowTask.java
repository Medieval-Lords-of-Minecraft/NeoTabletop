package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinTricksRowTask extends TheCrewTask {
	protected boolean completedRow = false;
	protected int consecutive = 0;
	
	protected boolean exclusive, negate;
	private int wins;
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "wins: " + wins + ", exclusive: " + exclusive + ", negate: " + negate);
		Util.msgRaw(s, "consecutive: " + consecutive + ", completedRow: " + completedRow);
	}
	
	public WinTricksRowTask(Configuration cfg) {
		super(cfg);

		wins = cfg.getInt("wins");
		exclusive = cfg.getBoolean("exclusive");
		negate = cfg.getBoolean("negate");
		display = "Win " + (exclusive ? "only &e" : "&e") + wins + " &ftricks in a row";
	}
	
	public WinTricksRowTask(TheCrewPlayer owner, WinTricksRowTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.negate = src.negate;
		this.wins = src.wins;
		this.exclusive = src.exclusive;
	}

	@Override
	public WinTricksRowTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinTricksRowTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.equals(owner)) {
			if (negate && !completedRow) { // Exclusive is never with negate
				return true;
			}
			else if (exclusive && completedRow) {
				return true;
			}
		}
		return false;
 	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.equals(owner) == negate) {
			consecutive = 0;
			return false;
		}
		else {
			if (exclusive && !completedRow) {
				completedRow = true;
			}
			else if (!exclusive) {
				return ++consecutive >= wins;
			}
		}
		
		return inst.getRoundsLeft() == 0 && completedRow;
	}

	@Override
	public void reset() {}
}
