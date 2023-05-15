package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public class WinCardsFinalTask extends TheCrewTask {
	protected CardMatcher card;
	
	@Override
	public void showDebug(CommandSender s) {
		Util.msgRaw(s, "card: " + card);
	}
	
	public WinCardsFinalTask(Configuration cfg) {
		super(cfg);
		card = new CardMatcher(cfg.getString("card"));
		
		display = "Win " + card.getDisplay() + " &fon the last trick of the game";
	}
	
	public WinCardsFinalTask(TheCrewPlayer owner, WinCardsFinalTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.display = src.display;
		this.card = src.card;
	}

	@Override
	public WinCardsFinalTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinCardsFinalTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (inst.getRoundsLeft() != 0) {
			for (TheCrewCard card : pile) {
				if (this.card.match(card)) {
					return true;
				}
			}
			return false;
		}
		else {
			return !winner.equals(owner);
		}
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		return inst.getRoundsLeft() == 0 && winner.equals(owner);
	}

	@Override
	public void reset() {}
}
