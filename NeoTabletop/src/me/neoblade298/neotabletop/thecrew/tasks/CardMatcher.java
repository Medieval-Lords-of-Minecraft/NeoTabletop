package me.neoblade298.neotabletop.thecrew.tasks;

import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;

public class CardMatcher {
	private int value;
	private CardType type;
	public CardMatcher(String matcher) {
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
}
