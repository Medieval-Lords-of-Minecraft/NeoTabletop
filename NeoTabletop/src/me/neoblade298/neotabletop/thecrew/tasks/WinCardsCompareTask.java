package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinCardsCompareTask extends TheCrewTask {
	protected int numWon = 0, numRemaining;
	
	protected CardMatcher card;
	protected boolean atLeast; // Either at least or equal
	protected int amount;
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "card: " + card + ", atLeast: " + atLeast + ", amount: " + amount + ", numWon: " + numWon + ", numRemaining: " + numRemaining);
	}
	
	public WinCardsCompareTask(Configuration cfg) {
		super(cfg);
		card = new CardMatcher(cfg.getString("card"));
		amount = cfg.getInt("amount");
		atLeast = cfg.getString("comparator").equals(">=");
		display = "Win " + (atLeast ? "at least&e " : "exactly&e ") + amount + " &f" + card.getDisplay();
	}
	
	public WinCardsCompareTask(TheCrewPlayer owner, WinCardsCompareTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		
		this.card = src.card;
		this.atLeast = src.atLeast;
		this.amount = src.amount;
		reset();
	}

	@Override
	public WinCardsCompareTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinCardsCompareTask(owner, this, inst);
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
				numRemaining--;
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
