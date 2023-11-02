package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.config.Configuration;

public class WinFirstTricksTask extends TheCrewTask {
	protected boolean negate;
	protected int amount;
	
	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "amount: " + amount + ", negate: " + negate);
	}
	
	public WinFirstTricksTask(Configuration cfg) {
		super(cfg);

		negate = cfg.getBoolean("negate");
		amount = cfg.getInt("amount");
		
		display = (negate ? "Do not win" : "Win") + " the first &e" + amount + " &ftricks";
	}
	
	public WinFirstTricksTask(TheCrewPlayer owner, WinFirstTricksTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.negate = src.negate;
		this.amount = src.amount;
	}

	@Override
	public WinFirstTricksTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinFirstTricksTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		// Since this isn't checked when the task is completed, we just need to check if the player wins (or loses if negated)
		return !winner.equals(owner) ^ negate;
 	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		// Just need to check if we've reached the round without losing
		return inst.getRound() == amount;
	}

	@Override
	public void reset() {}
}
