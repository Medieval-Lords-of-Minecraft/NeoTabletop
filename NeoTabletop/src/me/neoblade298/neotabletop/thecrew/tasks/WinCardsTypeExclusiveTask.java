package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.config.Configuration;

public class WinCardsTypeExclusiveTask extends TheCrewTask {
	private boolean hasWon = false;
	private int numExcluded = 0;
	private CardMatcher win;
	private CardMatcher exclude;
	
	public WinCardsTypeExclusiveTask(Configuration cfg) {
		super(null);
		win = new CardMatcher(cfg.getString("win"));
		exclude = new CardMatcher(cfg.getString("exclude"));

		display = "Win " + win.getDisplay() + " &fand no other card of that type";
	}
	
	public WinCardsTypeExclusiveTask(TheCrewPlayer owner, String display, CardMatcher win, CardMatcher exclude) {
		super(owner);
		this.win = win;
		this.exclude = exclude;
		this.display = display;
	}

	@Override
	public WinCardsTypeExclusiveTask clone(TheCrewPlayer owner) {
		return new WinCardsTypeExclusiveTask(owner, this.display, this.win, this.exclude);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card) && !winner.getUniqueId().equals(owner.getUniqueId())) {
				return true;
			}
			else if (exclude.match(card) && winner.getUniqueId().equals(owner.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card) && winner.getUniqueId().equals(owner.getUniqueId())) {
				hasWon = true;
			}
			else if (exclude.match(card) && winner.getUniqueId().equals(owner.getUniqueId())) {
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
