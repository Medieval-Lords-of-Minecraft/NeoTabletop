package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.kyori.adventure.text.Component;

import com.velocitypowered.api.command.CommandSource;
import me.neoblade298.neocore.shared.io.Section;

public class WinTricksTask extends TheCrewTask {
	protected int amount;
	
	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "amount: " + amount);
	}
	
	public WinTricksTask(Section cfg) {
		super(cfg);

		amount = cfg.getInt("amount");
		
		display = Component.text("Win exactly " + amount + " tricks");
	}
	
	public WinTricksTask(TheCrewPlayer owner, WinTricksTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.amount = src.amount;
	}

	@Override
	public WinTricksTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinTricksTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		return owner.getWins() > amount || owner.getWins() + inst.getRoundsLeft() < amount;
 	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		return inst.getRoundsLeft() == 0 && owner.getWins() == amount;
	}

	@Override
	public void reset() {}
}
