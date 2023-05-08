package me.neoblade298.neotabletop.thecrew;

import java.util.UUID;

import me.neoblade298.neotabletop.BooleanGameParameter;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;

public class TheCrewLobby extends GameLobby {

	public TheCrewLobby(String name, UUID host, boolean isPublic) {
		super(name, TheCrew.inst(), host, isPublic);
		minPlayers = 3;
		maxPlayers = 5;
		
		params.put("Hard Mode", new BooleanGameParameter("Hard Mode", 
				"If set to false, the server will block you from cards that will cause your"
				+ "game to become unwinnable.", false));
	}

	@Override
	public GameInstance onStart() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
