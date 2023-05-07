package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameInstance extends GameSession {
	protected HashMap<String, GamePlayer> players = new HashMap<String, GamePlayer>();
	protected HashSet<ProxiedPlayer> spectators = new HashSet<ProxiedPlayer>();
	public GameInstance(String name, Game game, UUID host, HashSet<UUID> players) {
		super(name, game, host);
		for (UUID uuid : players) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			this.players.put(p.getName().toLowerCase(), new GamePlayer(uuid, name, p));
		}
	}
	
	public abstract void handleLeave(GamePlayer gp);
	
	@Override
	public void kickPlayer(ProxiedPlayer p, String name) {
		if (!p.getUniqueId().equals(host)) {
			Util.msg(p, "&cOnly the host may kick other players!");
			return;
		}
		
		if (!players.containsKey(name.toLowerCase())) {
			Util.msg(p, "&cThat player isn't in your lobby!");
			return;
		}
		
		GamePlayer gp = players.remove(name.toLowerCase());
		handleLeave(gp);
		GameManager.removeFromSession(gp.getUniqueId());
		broadcast("&e" + p.getName() + " &7was kicked from the lobby!");
	}

	@Override
	public void broadcast(String msg) {
		for (GamePlayer gp : players.values()) {
			Util.msg(gp.getPlayer(), msg);
		}
		for (ProxiedPlayer p : spectators) {
			Util.msg(p, msg);
		}
	}

	@Override
	public void leavePlayer(ProxiedPlayer p) {
		if (players.containsKey(p.getName().toLowerCase())) {
			GamePlayer gp = players.remove(p.getName().toLowerCase());
			handleLeave(gp);
			GameManager.removeFromSession(p.getUniqueId());
			broadcast("&e" + p.getName() + " &7left the lobby!");
		}
		else if (spectators.contains(p)) {
			spectators.remove(p);
			GameManager.removeFromSession(p.getUniqueId());
			broadcast("&e" + p.getName() + " &7stopped spectating!");
		}
		else {
			Util.msg(p, "&cSomething went wrong! You were unable to leave lobby &e" + name + "&c.");
			return;
		}
		
		if (p.getUniqueId().equals(host)) {
			GamePlayer gp = players.values().iterator().next();
			host = gp.getUniqueId();
			broadcast("&7Because the host left, the new host is now &e" + gp.getName() + "&7!");
		}
	}
	
	public HashMap<String, GamePlayer> getPlayers() {
		return players;
	}
	
	public void addSpectator(ProxiedPlayer p) {
		spectators.add(p);
		broadcast("&e" + p.getName() + " &7began spectating!");
		onSpectate(p);
	}
	
	public abstract void onSpectate(ProxiedPlayer p);
}
