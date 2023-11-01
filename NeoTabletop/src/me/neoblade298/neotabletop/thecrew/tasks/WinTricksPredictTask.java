package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.config.Configuration;

public class WinTricksPredictTask extends TheCrewTask {
	protected int amount;
	
	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "amount: " + amount);
	}
	
	public WinTricksPredictTask(Configuration cfg) {
		super(cfg);
		
		display = "Predict exactly how many tricks you'll win at the start of the game";
	}
	
	public WinTricksPredictTask(TheCrewPlayer owner, WinTricksPredictTask src, TheCrewInstance inst, int amount) {
		super(owner, src, inst);

		display = "Win exactly " + amount + " tricks (Predicted at start of game)";
		this.amount = amount;
	}
	
	public WinTricksPredictTask clone(TheCrewPlayer owner, TheCrewInstance inst, int amount) {
		return new WinTricksPredictTask(owner, this, inst, amount);
	}

	@Override
	public WinTricksPredictTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinTricksPredictTask(owner, this, inst, 3); // Should never be used
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
