package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class NoOpenTrickTask extends TheCrewTask {
	protected ArrayList<CardMatcher> cards = new ArrayList<CardMatcher>();
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "cards: " + cards);
	}
	
	public NoOpenTrickTask(Configuration cfg) {
		super(cfg);
		
		for (String str : cfg.getStringList("cards")) {
			cards.add(new CardMatcher(str));
		}
		
		display = "Don't open a trick with " + cards.get(0).getDisplay();
		for (int i = 1; i < cards.size(); i++) {
			display += "&f, " + cards.get(i).getDisplay();
		}
	}
	
	public NoOpenTrickTask(TheCrewPlayer owner, NoOpenTrickTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.display = src.display;
		this.cards = src.cards;
	}

	@Override
	public NoOpenTrickTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new NoOpenTrickTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		// Check if player is winner and has no unmatched cards
		if (owner.equals(winner)) {
			for (TheCrewCard card : owner.getHand()) {
				boolean matches = false;
				for (CardMatcher cm : cards) {
					if (cm.match(card)) {
						matches = true;
						break;
					}
				}
				if (!matches) return false;
			}
			return true;
		}
		
		// Check if player opened with a matched card
		if (inst.getTurnOrder().get(0).equals(owner)) {
			for (CardMatcher cm : cards) {
				if (cm.match(owner.getLastPlayed())) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		return succeeds();
	}

	@Override
	public void reset() {}
	
	private boolean succeeds() {
		for (TheCrewCard card : owner.getHand()) {
			for (CardMatcher cm : cards) {
				if (cm.match(card)) {
					return false;
				}
			}
		}
		return true;
	}
}
