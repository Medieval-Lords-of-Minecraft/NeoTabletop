package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import net.md_5.bungee.config.Configuration;

public class WinCardsTask extends TheCrewTask {
	private boolean negate;
	private ArrayList<CardMatcher> cards = new ArrayList<CardMatcher>();
	
	public WinCardsTask(Configuration cfg) {
		for (String str : cfg.getStringList("cards")) {
			cards.add(new CardMatcher(str));
		}
		negate = cfg.getBoolean("negate", false);
	}

	@Override
	public WinCardsTask clone(TheCrewPlayer owner) {
		return null;
	}

	@Override
	public TaskResult onTrickEnd(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		ArrayList<Integer> toRemove = new ArrayList<Integer>(cards.size());
		for (TheCrewCardInstance card : pile) {
			for (int i = 0; i < cards.size(); i++) {
				CardMatcher matcher = cards.get(i);
				if (matcher.match(card)) {
					if (winner.getUniqueId().equals(owner.getUniqueId())) {
						toRemove.add(i);
					}
					else {
						return TaskResult.FAIL;
					}
				}
			}
		}
		
		if (cards.isEmpty()) {
			return TaskResult.SUCCESS;
		}
		return TaskResult.NONE;
	}

	@Override
	public TaskResult afterLastTrick(TheCrewInstance inst) {
		// TODO Auto-generated method stub
		return false;
	}
}
