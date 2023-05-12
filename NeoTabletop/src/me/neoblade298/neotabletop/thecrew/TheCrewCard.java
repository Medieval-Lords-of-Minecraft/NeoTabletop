package me.neoblade298.neotabletop.thecrew;

import net.md_5.bungee.api.ChatColor;

public class TheCrewCard {
	private CardType type;
	private int value;
	private String display;
	
	public TheCrewCard(CardType type, int value) {
		this.type = type;
		this.value = value;
		display = type.color + (this.type == CardType.SUB ? "&l&n" : "") + value;
	}
	
	public CardType getType() {
		return type;
	}
	public int getValue() {
		return value;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public boolean isSimilar(TheCrewCard comp) {
		return comp.getType() == type;
	}
	
	public enum CardType {
		RED(1, ChatColor.RED, "Red"),
		BLUE(2, ChatColor.BLUE, "Blue"),
		GREEN(3, ChatColor.GREEN, "Green"),
		YELLOW(4, ChatColor.YELLOW, "Yellow"),
		SUB(5, ChatColor.DARK_GRAY, "Sub");
		
		private final int sort;
		private final ChatColor color;
		private final String display;
		private CardType(final int sort, final ChatColor color, final String display) {
			this.sort = sort;
			this.color = color;
			this.display = display;
		}
		public int getSortPriority() {
			return sort;
		}
		public ChatColor getColor() {
			return color;
		}
		public String getDisplay() {
			return display;
		}
	}
	
	public TheCrewCardInstance createInstance() {
		return new TheCrewCardInstance(this);
	}
}
