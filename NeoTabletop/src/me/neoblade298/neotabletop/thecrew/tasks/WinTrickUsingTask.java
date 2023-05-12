package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinTrickUsingTask extends TheCrewTask {
	private int winRemaining, usingRemaining;
	
	private CardMatcher win, using;
	
	public WinTrickUsingTask(Configuration cfg) {
		super(null);
		win = new CardMatcher(cfg.getString("win"));
		using = new CardMatcher(cfg.getString("using"));
	}
	
	public WinTrickUsingTask(TheCrewPlayer owner, String display, CardMatcher win, CardMatcher using) {
		super(owner);
		
		this.display = display;
		this.win = win;
		this.using = using;
		reset();
	}

	@Override
	public WinTrickUsingTask clone(TheCrewPlayer owner) {
		return new WinTrickUsingTask(owner, display, win, using);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		boolean won = false, used = false;
		int numLost = 0, numUsed = 0;
		if (using.match(owner.getLastPlayed())) {
			numUsed = 1;
			used = true;
		}
		for (TheCrewCard card : pile) {
			if (win.match(card) && !card.equals(owner.getLastPlayed())) {
				numLost++;
				if (winner.equals(owner)) won = true;
			}
		}
		
		if (won && used) {
			return false;
		}
		
		return usingRemaining - numUsed == 0 || winRemaining - numLost == 0;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		boolean won = false, used = false;
		if (using.match(owner.getLastPlayed())) {
			usingRemaining--;
			used = true;
		}
		for (TheCrewCard card : pile) {
			if (win.match(card) && !card.equals(owner.getLastPlayed())) {
				winRemaining--;
				if (winner.equals(owner)) won = true;
			}
		}
		
		return won && used;
	}

	@Override
	public void reset() {
		winRemaining = win.getTotalCardsMatching();
		for (TheCrewCard card : owner.getHand()) {
			if (using.match(card)) {
				usingRemaining++;
				if (win.match(card)) {
					winRemaining--; // Can't win a card using the same card
				}
			}
		}
	}
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "win: " + win + ", winRemaining: " + winRemaining);
		Util.msgRaw(s, "using: " + using + ", usingRemaining: " + usingRemaining);
	}
}
