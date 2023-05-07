package me.neoblade298.neotabletop;

import java.util.UUID;

public interface Game {
	public String getName();
	public String getDescription();
	public GameLobby createLobby(String name, UUID uuid, boolean isPublic);
}
