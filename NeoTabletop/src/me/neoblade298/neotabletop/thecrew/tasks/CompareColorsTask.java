package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.config.Configuration;

public class CompareColorsTask extends TheCrewTask {
	protected CardMatcher card, comp;
	protected boolean more, allowZero; // If true, win more card than comp. False, win equal card and comp
	protected int cardsLeft = 9, compsLeft = 9;

	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "card: " + card + ", comp: " + comp + ", more: " + more + ", allowZero: " + allowZero + ", cardsLeft: " +
				cardsLeft + ", compsLeft: " + compsLeft);
	}
	
	public CompareColorsTask(Configuration cfg) {
		super(cfg);
		card = new CardMatcher(cfg.getString("card"));
		comp = new CardMatcher(cfg.getString("comp"));
		more = cfg.getString("comparator").equals(">");
		allowZero = cfg.getBoolean("allow_zero");

		if (more) {
			display = "Win more " + card.getDisplay() + " &fthan " + comp.getDisplay() + " &fin one trick.";
		}
		else {
			display = "Win as many " + card.getDisplay() + " &fas " + comp.getDisplay() + " &fin one trick.";
		}
		
		if (allowZero) {
			display += " &7&o*0 cards is allowed.";
		}
		else {
			display += " &7&o*0 cards is not allowed.";
		}
	}
	
	public CompareColorsTask(TheCrewPlayer owner, CompareColorsTask src, TheCrewInstance inst) {
		super(owner, src, inst);
		this.card = src.card;
		this.comp = src.comp;
		this.more = src.more;
		this.allowZero = src.allowZero;
	}

	@Override
	public CompareColorsTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new CompareColorsTask(this.owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		int cardsWon = 0;
		int compsWon = 0;
		for (TheCrewCardInstance card : pile) {
			if (this.card.match(card)) {
				cardsWon++;
			}
			else if (this.comp.match(card)) {
				compsWon++;
			}
		}
		
		if (succeeds(cardsWon, compsWon)) {
			return false;
		}
		
		cardsLeft -= cardsWon;
		compsLeft -= compsWon;
		return !canStillSucceed();
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (!winner.getUniqueId().equals(owner.getUniqueId())) {
			return false;
		}
		
		// Only time update happens is if owner is winner
		int cardsWon = 0;
		int compsWon = 0;
		for (TheCrewCardInstance card : pile) {
			if (this.card.match(card)) {
				cardsWon++;
			}
			else if (this.comp.match(card)) {
				compsWon++;
			}
		}
		
		return succeeds(cardsWon, compsWon);
	}

	@Override
	public void reset() {
		cardsLeft = 9;
		compsLeft = 9;
	}
	
	private boolean succeeds(int cardsWon, int compsWon) {
		if (cardsWon == 0) return false;
		if (!allowZero && compsWon == 0) return false;
		
		if (more && cardsWon > compsWon || !more && cardsWon == compsWon) {
			return true;
		}
		return false;
	}
	
	private boolean canStillSucceed() {
		int amt = allowZero ? 0 : 1;
		if (more) {
			return cardsLeft > amt && compsLeft > amt - 1;
		}
		else {
			return cardsLeft > amt && compsLeft > amt;
		}
	}
}
