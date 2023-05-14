package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinEdgeTricksTask extends TheCrewTask {
	protected boolean first, last, exclusive;
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "first: " + first + ", last: " + last + ", exclusive: " + exclusive);
	}
	
	public WinEdgeTricksTask(Configuration cfg) {
		super(cfg);

		first = cfg.getBoolean("first");
		last = cfg.getBoolean("last");
		exclusive = cfg.getBoolean("exclusive");
		
		String str;
		if (first && last) {
			str = "first and last";
		}
		else if (first) {
			str = "first";
		}
		else {
			str = "last";
		}
		
		display = "Win " + (exclusive ? "only" : "") + " the " + str + " trick";
	}
	
	public WinEdgeTricksTask(TheCrewPlayer owner, WinEdgeTricksTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.display = src.display;
		this.first = src.first;
		this.last = src.last;
		this.exclusive = src.exclusive;
	}

	@Override
	public WinEdgeTricksTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinEdgeTricksTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		int round = inst.getRound(), roundsLeft = inst.getRoundsLeft();
		if (winner.equals(owner) && exclusive) {
			if (first && last) {
				return round != 0 && roundsLeft != 0;
			}
			else if (first) {
				return round != 0;
			}
			else {
				return roundsLeft != 0;
			}
		}
		
		else if (!winner.equals(owner)) {
			if (first && round == 0) {
				return true;
			}
			else if (last && roundsLeft == 0) {
				return true;
			}
		}
		return false;
 	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.equals(owner) && !exclusive && first && !last && inst.getRound() == 0) {
			return true;
		}
		
		if (inst.getRoundsLeft() == 0) {
			return true; // If hasFailed hasn't stopped the game, we've completed this task
		}
		return false;
	}

	@Override
	public void reset() {}
}
