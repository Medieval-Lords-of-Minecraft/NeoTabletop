package me.neoblade298.neotabletop.thecrew;

import java.util.UUID;

import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.BooleanParameterSetter;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameParameter;

public class TheCrewLobby extends GameLobby {

	public TheCrewLobby(String name, UUID host, boolean isPublic) {
		super(name, TheCrew.inst(), host, isPublic);
		
		params.put("Hard Mode", new GameParameter("Hard Mode", 
				"If set to false, the server will block you from cards that will cause your"
				+ "game to become unwinnable.", false, new BooleanParameterSetter()));
		
		params.put("Difficulty", new GameParameter("Difficulty",
				"Affects how many conditions you must pass to win. Can be 1-25.",
				Integer.valueOf(6), (str) -> {
					if (!SharedUtil.isNumeric(str)) {
						return null;
					}
					Integer i = Integer.parseInt(str);
					if (i < 1 || i > 25) {
						return null;
					}
					return i;
				}));
	}

	@Override
	public GameInstance onStart() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
