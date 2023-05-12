package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.config.Configuration;

public class WinCardsTypeExclusive extends TheCrewTask {
	private boolean hasWon = false;
	private CardMatcher win;
	private CardMatcher exclude;
	
	public WinCardsTypeExclusive(Configuration cfg) {
		super(null);
		win = new CardMatcher(cfg.getString("win"));
		exclude = new CardMatcher(cfg.getString("exclude"));

		display = "Win " + win.getDisplay() + " &fand no other card of that type";
	}
	
	public WinCardsTypeExclusive(TheCrewPlayer owner, CardMatcher win, CardMatcher exclude) {
		super(owner);
		this.win = win;
		this.exclude = exclude;
	}

	@Override
	public WinCardsTypeExclusive clone(TheCrewPlayer owner) {
		return new WinCardsTypeExclusive(owner, this.win, this.exclude);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card) && !winner.getUniqueId().equals(owner.getUniqueId()) {
				return true;
			}
			else if (exclude.match(card) && winner.getUniqueId().equals(owner.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick) {
		for (TheCrewCardInstance card : pile) {
			if (win.match(card) && winner.getUniqueId().equals(owner.getUniqueId())) {
				hasWon = true;
			}
		}
		return false;
	}

	@Override
	public void reset() {
		hasWon = false;
	}
}
