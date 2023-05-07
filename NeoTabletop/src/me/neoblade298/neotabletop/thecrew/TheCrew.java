package me.neoblade298.neotabletop.thecrew;

import java.util.UUID;

import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameLobby;

public class TheCrew implements Game {
	private static TheCrew inst;
	
	public void init() {
		inst = this;
	}

	@Override
	public String getName() {
		return "The Crew";
	}

	@Override
	public String getDescription() {
		return "Trick-taking cooperative card game.";
	}
	
	public static Game inst() {
		return inst;
	}

	@Override
	public GameLobby createLobby(String name, UUID uuid, boolean isPublic) {
		return new TheCrewLobby(name, uuid, false);
	}
}
