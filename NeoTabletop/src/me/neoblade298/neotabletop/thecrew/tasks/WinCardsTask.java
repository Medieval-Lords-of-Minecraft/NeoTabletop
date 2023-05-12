package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.config.Configuration;

public class WinCardsTask extends TheCrewTask {
	private boolean negate = false;
	private ArrayList<CardMatcher> cards = new ArrayList<CardMatcher>(), completed = new ArrayList<CardMatcher>();
	
	public WinCardsTask(Configuration cfg) {
		super(null);
		for (String str : cfg.getStringList("cards")) {
			cards.add(new CardMatcher(str));
		}
		negate = cfg.getBoolean("negate", false);
		
		display = "Win ";
		for (int i = 0; i < cards.size(); i++) {
			if (i != 0) {
				display += "&f, ";
			}
			display += cards.get(i).getDisplay();
		}
	}
	
	public WinCardsTask(TheCrewPlayer owner, ArrayList<CardMatcher> cards, boolean negate) {
		super(owner);
		for (CardMatcher card : cards) {
			this.cards.add(card);
		}
		this.negate = negate;
	}

	@Override
	public WinCardsTask clone(TheCrewPlayer owner) {
		return new WinCardsTask(owner, this.cards, this.negate);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick) {
		for (TheCrewCardInstance card : pile) {
			for (int i = 0; i < cards.size(); i++) {
				CardMatcher matcher = cards.get(i);
				if (matcher.match(card)) {
					if (!winner.getUniqueId().equals(owner.getUniqueId()) ^ negate) {
						// 1: Winner isn't player and negate is false
						// 2: winner is player and negate is true
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile, boolean lastTrick) {
		ArrayList<Integer> toRemove = new ArrayList<Integer>(cards.size());
		for (TheCrewCardInstance card : pile) {
			for (int i = 0; i < cards.size(); i++) {
				CardMatcher matcher = cards.get(i);
				if (matcher.match(card)) {
					if (winner.getUniqueId().equals(owner.getUniqueId())) { // Only remove cards without negate (since negate matches multiple)
						toRemove.add(i);
					}
				}
			}
		}
		
		for (int i : toRemove) {
			completed.add(cards.remove(i));
		}

		return cards.isEmpty();
	}

	@Override
	public void reset() {
		cards.addAll(completed);
		completed.clear();
		
	}
}
