package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.kyori.adventure.text.Component;

import com.velocitypowered.api.command.CommandSource;
import me.neoblade298.neocore.shared.io.Section;

public class WinTrickUsingTask extends TheCrewTask {
	protected int winRemaining, usingRemaining;
	
	protected CardMatcher win, using;
	
	public WinTrickUsingTask(Section cfg) {
		super(cfg);
		if (cfg.contains("win")) {
			win = new CardMatcher(cfg.getString("win"));
		}
		using = new CardMatcher(cfg.getString("using"));
		
		if (win == null) {
			display = Component.text("Win a trick using ").append(using.getDisplay());
		}
		else {
			display = Component.text().content("Win ")
					.append(win.getDisplay())
					.append(Component.text(" using "))
					.append(using.getDisplay()).build();
		}
	}
	
	public WinTrickUsingTask(TheCrewPlayer owner, WinTrickUsingTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		
		this.win = src.win;
		this.using = src.using;
		reset();
	}

	@Override
	public WinTrickUsingTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinTrickUsingTask(owner, this, inst);
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
			if (win == null || (win.match(card) && !card.equals(owner.getLastPlayed()))) {
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
			if (win == null || (win.match(card) && !card.equals(owner.getLastPlayed()))) {
				winRemaining--;
				if (winner.equals(owner)) won = true;
			}
		}
		
		return won && used;
	}

	@Override
	public void reset() {
		winRemaining = win != null ? win.getTotalCardsMatching() : 40;
		for (TheCrewCard card : owner.getHand()) {
			if (using.match(card)) {
				usingRemaining++;
			}
			
			if (win != null && win.match(card)) {
				winRemaining--; // Can't win a card that's in your hand (have to be using something else)
			}
		}
	}
	
	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "win: " + win + ", winRemaining: " + winRemaining);
		Util.msgRaw(s, "using: " + using + ", usingRemaining: " + usingRemaining);
	}
}
