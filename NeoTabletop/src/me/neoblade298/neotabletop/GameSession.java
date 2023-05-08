package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameSession {
	protected String name;
	protected Game game;
	protected UUID host;
	protected HashMap<String, GameParameter> params = new HashMap<String, GameParameter>();
	public GameSession(String name, Game game, UUID host) {
		this.name = name;
		this.game = game;
		this.host = host;
	}
	public GameSession(String name, Game game, UUID host, HashMap<String, GameParameter> params) {
		this(name, game, host);
		this.params = params;
	}
	public abstract void broadcast(String msg);
	public String getName() {
		return name;
	}
	public Game getGame() {
		return game;
	}
	public UUID getHost() {
		return host;
	}
	public HashMap<String, GameParameter> getParameters() {
		return params;
	}
	public abstract void leavePlayer(ProxiedPlayer p);
	public abstract void kickPlayer(ProxiedPlayer s, String username);
	public abstract void displayInfo(ProxiedPlayer p);
}
