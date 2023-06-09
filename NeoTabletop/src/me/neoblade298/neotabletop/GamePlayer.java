package me.neoblade298.neotabletop;

import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GamePlayer {
	protected UUID uuid;
	protected String name;
	protected ProxiedPlayer p;
	public GamePlayer(UUID uuid, ProxiedPlayer p) {
		super();
		this.uuid = uuid;
		this.name = p.getName();
		this.p = p;
	}
	public UUID getUniqueId() {
		return uuid;
	}
	public String getName() {
		return name;
	}
	public ProxiedPlayer getPlayer() {
		return p;
	}
	@Override
	public String toString() {
		return name;
	}
}
