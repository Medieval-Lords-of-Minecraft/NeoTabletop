package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import net.md_5.bungee.config.Configuration;

public class WinOneColor extends TheCrewTask {
	private HashMap<CardType, Integer> numWon = new HashMap<CardType, Integer>();
	private HashSet<CardType> colorsRemaining = new HashSet<CardType>();
	
	public WinOneColor(Configuration cfg) {
		super(null);
		display = "Win all cards of at least one color";
	}
	
	public WinOneColor(TheCrewPlayer owner, String display) {
		super(owner);
		reset();
	}

	@Override
	public WinOneColor clone(TheCrewPlayer owner) {
		return new WinOneColor(owner, display);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		if (winner.getUniqueId().equals(owner.getUniqueId())) {
			return false; // Can only fail if not winner
		}
		
		HashSet<CardType> toRemove = new HashSet<CardType>();
		int colorsRemoved = 0;
		for (TheCrewCard card : pile) {
			if (!toRemove.contains(card.getType()) && colorsRemaining.contains(card.getType())) {
				toRemove.add(card.getType());
				colorsRemoved++; // Remove a color if it's in colorsRemaining and hasn't been removed already
			}
		}
		
		if (colorsRemaining.size() == colorsRemoved) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		for (TheCrewCard card : pile) {
			colorsRemaining.remove(card.getType());
			if (winner.getUniqueId().equals(owner.getUniqueId())) {
				int put = numWon.getOrDefault(card.getType(), 0) + 1;
				numWon.put(card.getType(), put);
				
				if (put == 9) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void reset() {
		for (CardType type : CardType.values()) {
			if (type == CardType.SUB) continue;
			colorsRemaining.add(type);
		}
		numWon.clear();
	}
}
