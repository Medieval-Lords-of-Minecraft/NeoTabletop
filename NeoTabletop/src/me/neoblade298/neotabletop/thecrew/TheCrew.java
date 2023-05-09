package me.neoblade298.neotabletop.thecrew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.NeoTabletop;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;

public class TheCrew extends Game {
	private static TheCrew inst;
	private static File BASE_DIR = new File(NeoTabletop.inst().getDataFolder(), "/The Crew");
	private static ArrayList<TheCrewCard> deck = new ArrayList<TheCrewCard>(44);
	public static ArrayList<TheCrewTask> tasks = new ArrayList<TheCrewTask>();
	
	public TheCrew() {
		super(BASE_DIR);
		inst = this;
		
		// Set up deck of cards
		for (int i = 1; i <= 9; i++) {
			deck.add(new TheCrewCard(CardType.RED, i));
			deck.add(new TheCrewCard(CardType.BLUE, i));
			deck.add(new TheCrewCard(CardType.GREEN, i));
			deck.add(new TheCrewCard(CardType.YELLOW, i));
			if (i <= 4) deck.add(new TheCrewCard(CardType.WILD, i));
		}
	}
	
	public static Game inst() {
		return inst;
	}

	@Override
	public GameLobby createLobby(String name, UUID uuid, boolean isPublic) {
		return new TheCrewLobby(name, uuid, false);
	}
	
	public static ArrayList<TheCrewCard> createDeck() {
		ArrayList<TheCrewCard> deck = new ArrayList<TheCrewCard>(TheCrew.deck);
		Collections.shuffle(deck);
		return deck;
	}
}
