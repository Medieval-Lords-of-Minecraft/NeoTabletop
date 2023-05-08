package me.neoblade298.neotabletop.thecrew;

import java.io.File;
import java.util.UUID;

import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.NeoTabletop;

public class TheCrew extends Game {
	private static TheCrew inst;
	private static File BASE_DIR = new File(NeoTabletop.inst().getDataFolder(), "/The Crew");
	
	public TheCrew() {
		super(BASE_DIR);
		inst = this;
	}
	
	public static Game inst() {
		return inst;
	}

	@Override
	public GameLobby createLobby(String name, UUID uuid, boolean isPublic) {
		return new TheCrewLobby(name, uuid, false);
	}
}
