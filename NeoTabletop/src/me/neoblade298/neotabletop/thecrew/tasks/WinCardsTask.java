package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinCardsTask extends TheCrewTask {
	protected boolean negate = false;
	protected ArrayList<CardMatcher> cards = new ArrayList<CardMatcher>(), completed = new ArrayList<CardMatcher>();
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "cards: " + cards);
		Util.msgRaw(s, "completed: " + completed);
		Util.msgRaw(s, "negate: " + negate);
	}
	
	public WinCardsTask(Configuration cfg) {
		super(cfg);
		for (String str : cfg.getStringList("cards")) {
			cards.add(new CardMatcher(str));
		}
		negate = cfg.getBoolean("negate", false);
		
		display = negate ? "Don't win " : "Win ";
		for (int i = 0; i < cards.size(); i++) {
			if (i != 0) {
				display += "&f, ";
			}
			display += cards.get(i).getDisplay();
		}
	}
	
	public WinCardsTask(TheCrewPlayer owner, WinCardsTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		
		for (CardMatcher card : src.cards) {
			this.cards.add(card);
		}
		this.negate = src.negate;
	}

	@Override
	public WinCardsTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinCardsTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
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
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		ArrayList<Integer> toComplete = new ArrayList<Integer>(cards.size());
		for (TheCrewCard card : pile) {
			for (int i = 0; i < cards.size(); i++) {
				CardMatcher matcher = cards.get(i);
				if (matcher.match(card)) {
					if (winner.getUniqueId().equals(owner.getUniqueId()) && !negate) { // Only remove cards without negate (since negate matches multiple)
						toComplete.add(i);
					}
				}
			}
		}
		
		for (int i : toComplete) {
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
