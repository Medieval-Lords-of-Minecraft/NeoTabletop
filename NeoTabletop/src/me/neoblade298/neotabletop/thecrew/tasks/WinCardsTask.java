package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.config.Configuration;

public class WinCardsTask extends TheCrewTask {
	private boolean negate;
	
	public WinCardsTask(Configuration cfg) {
		
	}

	@Override
	public WinCardsTask clone(TheCrewPlayer owner) {
		return null;
	}

	@Override
	public boolean onTrickEnd(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCard> pile) {
		
		return false;
	}

	@Override
	public boolean afterLastTrick(TheCrewInstance inst) {
		// TODO Auto-generated method stub
		return false;
	}
}
