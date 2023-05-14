package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinCardsCompareMultipleTask extends TheCrewTask {
	private HashMap<CardType, Integer> numWon = new HashMap<CardType, Integer>(), numRemaining = new HashMap<CardType, Integer>();
	
	protected ArrayList<CardMatcher> cards = new ArrayList<CardMatcher>();
	protected int amount;
	
	@Override
	public void showDebug(CommandSender s) {
		String text = "cards:";
		for (CardMatcher cm : cards) text += " " + cm;
		Util.msgRaw(s, text);
		Util.msgRaw(s, "amount: " + amount);
		Util.msgRaw(s, "numWon: " + numWon);
		Util.msgRaw(s, "numRemaining: " + numRemaining);
	}
	
	public WinCardsCompareMultipleTask(Configuration cfg) {
		super(cfg);
		amount = cfg.getInt("amount");
		
		display = "Win exactly &e1 &f" + cards.get(0).getDisplay() + " &fand exactly &e1 &f" + cards.get(1).getDisplay();
	}
	
	public WinCardsCompareMultipleTask(TheCrewPlayer owner, WinCardsCompareMultipleTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		
		this.cards = src.cards;
		this.amount = src.amount;
		reset();
	}

	@Override
	public WinCardsCompareMultipleTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinCardsCompareMultipleTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.getUniqueId().equals(owner.getUniqueId())) {
			HashMap<CardType, Integer> numWon = new HashMap<CardType, Integer>();
			for (TheCrewCard card : pile) {
				for (CardMatcher matcher : cards) {
					if (matcher.match(card)) {
						numWon.put(card.getType(), numWon.getOrDefault(card.getType(), 0) + 1);
					}
				}
			}
			
			for (CardMatcher cm : cards) {
				if (numWon.getOrDefault(cm.getType(), 0) + this.numWon.getOrDefault(cm.getType(), 0) > amount) {
					return true; // Owner wins more cards than the exact amount
				}
			}
		}
		else {
			HashMap<CardType, Integer> numLost = new HashMap<CardType, Integer>();
			for (TheCrewCard card : pile) {
				for (CardMatcher matcher : cards) {
					if (matcher.match(card)) {
						numLost.put(card.getType(), numWon.getOrDefault(card.getType(), 0) + 1);
					}
				}
			}

			for (CardMatcher cm : cards) {
				CardType t = cm.getType();
				int numPossible = this.numWon.getOrDefault(t, 0) + numRemaining.getOrDefault(t, 0)
				- numLost.getOrDefault(t, 0);
				if (numPossible < amount) { // Owner can't possibly win enough cards to reach amount
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCard card : pile) {
			for (CardMatcher matcher : cards) {
				if (matcher.match(card)) {
					if (winner.getUniqueId().equals(owner.getUniqueId())) {
						numWon.put(matcher.getType(), numWon.getOrDefault(matcher.getType(), 0) + 1);
					}
					else {
						numRemaining.put(matcher.getType(), numRemaining.get(matcher.getType()) - 1);
					}
				}
			}
		}
		
		boolean success = true;
		for (CardMatcher matcher : cards) {
			CardType t = matcher.getType();
			if (numWon.getOrDefault(t, 0) != amount || numRemaining.get(t) != 0) {
				success = false;
			}
		}
		return success;
	}

	@Override
	public void reset() {
		numWon.clear();
		for (CardMatcher cm : cards) {
			numRemaining.put(cm.getType(), cm.getTotalCardsMatching());
		}
	}
}
