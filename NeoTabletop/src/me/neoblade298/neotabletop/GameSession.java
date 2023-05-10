package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameSession<T extends GamePlayer> {
	protected String name;
	protected Game game;
	protected UUID host;
	protected boolean isPublic;
	protected HashMap<String, GameParameter> params = new HashMap<String, GameParameter>();
	public GameSession(String name, Game game, UUID host, boolean isPublic) {
		this.name = name;
		this.game = game;
		this.host = host;
		this.isPublic = isPublic;
	}
	public GameSession(String name, Game game, UUID host, boolean isPublic, HashMap<String, GameParameter> params) {
		this(name, game, host, isPublic);
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
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setHost(CommandSender cmdUser, String username) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(username);
		if (p == null) {
			Util.msg(cmdUser, "&cThat player isn't online!");
			return;
		}
		
		host = p.getUniqueId();
		broadcast("&e" + cmdUser.getName() + " &7set the game's host to &e" + p.getName());
	}
	public HashMap<String, GameParameter> getParameters() {
		return params;
	}
	public abstract void leavePlayer(ProxiedPlayer p);
	public abstract void kickPlayer(ProxiedPlayer s, String username);
	public abstract void adminKickPlayer(CommandSender s, String username);
	public abstract void displayInfo(ProxiedPlayer viewer, ProxiedPlayer viewed);
}
