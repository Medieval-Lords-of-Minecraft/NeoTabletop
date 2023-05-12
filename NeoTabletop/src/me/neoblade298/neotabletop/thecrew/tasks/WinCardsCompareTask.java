package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import net.md_5.bungee.config.Configuration;

public class WinCardsCompareTask extends TheCrewTask {
	private int numWon = 0, numRemaining;
	
	private CardMatcher card;
	private boolean atLeast; // Either at least or equal
	private int amount;
	
	public WinCardsCompareTask(Configuration cfg) {
		super(null);
		card = new CardMatcher(cfg.getString("card"));
		amount = cfg.getInt("amount");
		atLeast = cfg.getString("comparator").equals(">=");
		display = "Win " + (atLeast ? "at least" : "exactly") + amount + " of " + card.getDisplay();
	}
	
	public WinCardsCompareTask(TheCrewPlayer owner, String display, CardMatcher card, boolean atLeast, int amount) {
		super(owner);
		
		this.display = display;
		this.card = card;
		this.atLeast = atLeast;
		this.amount = amount;
		reset();
	}

	@Override
	public WinCardsCompareTask clone(TheCrewPlayer owner) {
		return new WinCardsCompareTask(owner, display, card, atLeast, amount);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.getUniqueId().equals(owner.getUniqueId())) {
			int numWon = 0;
			for (TheCrewCard card : pile) {
				if (this.card.match(card)) {
					numWon++;
				}
			}
			
			if (!atLeast && this.numWon + numWon > amount) {
				return true; // Owner wins more cards than the exact amount
			}
		}
		else {
			int numLost = 0;
			for (TheCrewCard card : pile) {
				if (this.card.match(card)) {
					numLost++;
				}
			}
			
			int numPossible = this.numWon + numRemaining - numLost;
			if (numPossible < amount) { // Owner can't possibly win enough cards to reach amount
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCard card : pile) {
			if (this.card.match(card)) {
				if (winner.getUniqueId().equals(owner.getUniqueId())) {
					numWon++;
				}
				else {
					numRemaining--;
				}
			}
		}
		
		if (atLeast && numWon >= amount) {
			return true;
		}
		else if (!atLeast && numWon == amount && numRemaining == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
		numWon = 0;
		numRemaining = card.getTotalCardsMatching();
	}
}
