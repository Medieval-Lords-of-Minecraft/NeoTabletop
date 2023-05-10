package me.neoblade298.neotabletop.thecrew;

import net.md_5.bungee.api.ChatColor;

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
	
	public String getDisplay() {
		return type.color + "" + value;
	}
	
	public boolean isSimilar(TheCrewCard comp) {
		return comp.getType() == type;
	}
	
	public enum CardType {
		RED(1, ChatColor.RED),
		BLUE(2, ChatColor.BLUE),
		GREEN(3, ChatColor.GREEN),
		YELLOW(4, ChatColor.YELLOW),
		SUB(5, ChatColor.DARK_GRAY);
		
		private final int sort;
		private final ChatColor color;
		private CardType(final int sort, final ChatColor color) {
			this.sort = sort;
			this.color = color;
		}
		public int getSortPriority() {
			return sort;
		}
		public ChatColor getColor() {
			return color;
		}
	}
}
