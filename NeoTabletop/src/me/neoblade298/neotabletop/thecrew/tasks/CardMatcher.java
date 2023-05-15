package me.neoblade298.neotabletop.thecrew.tasks;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;

public class CardMatcher {
	private int value;
	private CardType type;
	private String display, base;
	private int totalCardsMatching;
	public CardMatcher(String matcher) {
		if (matcher == null) {
			value = -1;
			type = null;
			return;
		}
		
		this.base = matcher;
		char num = matcher.charAt(0);
		if (num == '*') {
			value = -1;
		}
		else {
			value = Integer.parseInt("" + num);
		}
		
		char t = matcher.charAt(1);
		if (t == '*') {
			type = null;
		}
		else {
			switch (t) {
			case 'R': type = CardType.RED;
			break;
			case 'G': type = CardType.GREEN;
			break;
			case 'Y': type = CardType.YELLOW;
			break;
			case 'B': type = CardType.BLUE;
			break;
			case 'S': type = CardType.SUB;
			break;
			}
		}
		
		// Calculate total cards matching
		
		// Calculate display
		if (value == -1 && type == null) {
			display = "Any Card";
			totalCardsMatching = 40;
		}
		else if (value == -1) {
			display = type.getColor() + "Any " + type.getDisplay() + " Card";
			totalCardsMatching = (type == CardType.SUB ? 4 : 9);
		}
		else if (type == null) {
			display = "Any " + value + " Card";
			totalCardsMatching = 4;
		}
		else {
			display = type.getColor() + type.getDisplay() + " " + value;
			totalCardsMatching = 1;
		}
	}
	
	public boolean match(TheCrewCard card) {
		if (value != -1 && card.getValue() != value) {
			return false;
		}
		if (type != null && card.getType() != type) {
			return false;
		}
		return true;
	}
	
	public CardType getType() {
		return type;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public int getTotalCardsMatching() {
		return totalCardsMatching;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CardMatcher) {
			CardMatcher cm = (CardMatcher) o;
			return cm.getType() == type && cm.value == value;
		}
		return false;
	}
	
	public String toString() {
		return base;
	}
}
