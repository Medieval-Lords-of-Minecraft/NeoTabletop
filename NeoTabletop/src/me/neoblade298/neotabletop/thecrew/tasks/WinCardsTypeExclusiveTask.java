package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinCardsTypeExclusiveTask extends TheCrewTask {
	protected boolean hasWon = false;
	protected int numExcluded = 0;
	protected CardMatcher win, exclude;
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "hasWon: " + hasWon + ", numExcluded: " + numExcluded + ", win: " + win + ", exclude: " + exclude);
	}
	
	public WinCardsTypeExclusiveTask(Configuration cfg) {
		super(cfg);
		win = new CardMatcher(cfg.getString("win"));
		exclude = new CardMatcher(cfg.getString("exclude"));

		display = "Win " + win.getDisplay() + " &fand no other card of that type";
	}
	
	public WinCardsTypeExclusiveTask(TheCrewPlayer owner, WinCardsTypeExclusiveTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		this.win = src.win;
		this.exclude = src.exclude;
		this.display = src.display;
	}

	@Override
	public WinCardsTypeExclusiveTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinCardsTypeExclusiveTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card)) {
				if (!winner.equals(owner)) {
					return true;
				}
			}
			else if (exclude.match(card) && winner.equals(owner)) { // Only lose if it DOESN'T match win (since they're same matcher)
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card)) {
				hasWon = winner.equals(owner);
			}
			else if (exclude.match(card) && !winner.equals(owner)) {
				numExcluded++;
			}
		}
		
		// This task ONLY applies to subs
		if (numExcluded == 3 && hasWon) {
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
		hasWon = false;
	}
}
