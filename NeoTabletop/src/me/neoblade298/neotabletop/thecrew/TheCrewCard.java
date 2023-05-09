package me.neoblade298.neotabletop.thecrew;

public class TheCrewCard {
	private CardType type;
	private int value;
	
	public TheCrewCard(CardType type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public CardType getType() {
		return type;
	}
	public int getValue() {
		return value;
	}
	
	public enum CardType {
		RED,
		BLUE,
		GREEN,
		YELLOW,
		WILD;
	}
}
