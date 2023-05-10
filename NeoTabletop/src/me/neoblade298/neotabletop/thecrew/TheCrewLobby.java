package me.neoblade298.neotabletop.thecrew;

import java.util.UUID;

import me.neoblade298.neotabletop.BooleanParameterSetter;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameParameter;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.IntegerParameterSetter;

public class TheCrewLobby extends GameLobby<TheCrewPlayer> {

	public TheCrewLobby(String name, UUID host, boolean isPublic) {
		super(name, TheCrew.inst(), host, isPublic);
		
		params.put("hard_mode", new GameParameter("hard_mode", "Hard Mode", 
				"If set to false, the server will block you from playing cards that will cause your"
				+ "game to become unwinnable.", false, new BooleanParameterSetter()));
		
		params.put("difficulty", new GameParameter("difficulty", "Difficulty",
				"Affects how many conditions you must pass to win. Can be 1-25.",
				Integer.valueOf(6), new IntegerParameterSetter(1, 25)));
		
		params.put("sonar_tokens", new GameParameter("sonar_tokens", "Sonar Tokens",
				"Number of sonar tokens you're allowed to use to communicate. Can be 0-3",
				Integer.valueOf(1), new IntegerParameterSetter(0, 3)));
	}

	@Override
	public GameInstance<? extends GamePlayer> onStart() {
		return new TheCrewInstance(host, null);
	}
	
}
