package me.neoblade298.neotabletop.thecrew;

public class TheCrewCardInstance extends TheCrewCard {
	private TheCrewPlayer player;
	public TheCrewCardInstance(TheCrewCard card) {
		super(card.getType(), card.getValue());
	}
	
	public void setPlayer(TheCrewPlayer p) {
		this.player = p;
	}
	
	public TheCrewPlayer getPlayer() {
		return player;
	}
}
