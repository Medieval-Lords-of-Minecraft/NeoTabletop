package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameLobby extends GameSession {
	protected HashSet<UUID> players, invited;
	protected int minPlayers = 1, maxPlayers = 4;
	protected boolean isPublic;
	
	public GameLobby(String name, Game game, UUID host, boolean isPublic) {
		super(name, game, host);
		this.isPublic = isPublic;
	}
	
	public GameInstance startGame(ProxiedPlayer s) {
		if (!s.getUniqueId().equals(host)) {
			Util.msg(s, "&cOnly the host may start the game!");
			return null;
		}
		
		if (minPlayers > players.size()) {
			Util.msg(s, "&cYou need at least &e" + minPlayers + " &cplayers to start!");
			return null;
		}
		return onStart();
	}
	
	public abstract GameInstance onStart();

	public void invitePlayer(ProxiedPlayer s, String username) {
		if (!s.getUniqueId().equals(host)) {
			Util.msg(s, "&cOnly the host may invite other players!");
			return;
		}
		
		ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(username);
		if (recipient == null) {
			Util.msg(s, "&cThat player isn't online!");
			return;
		}
		
		invited.add(recipient.getUniqueId());
		broadcast("&e" + recipient.getName() + " &7was invited to the lobby!");
		
		// TODO create clickable chat to join
		Util.msg(recipient, "You've been invited to lobby &e" + name + " &7for &e" + game.getName() + "&7!");
	}
	
	public void addPlayer(ProxiedPlayer p) {
		if (!isPublic && invited.contains(p.getUniqueId())) {
			Util.msg(p, "&cYou aren't invited to this private lobby!");
			return;
		}
		
		if (maxPlayers <= players.size()) {
			Util.msg(p, "&cThis lobby is full as it has a max of &e" + maxPlayers + " &cplayers!");
		}
		
		if (!isPublic) {
			invited.remove(p.getUniqueId());
		}

		players.add(p.getUniqueId());
		broadcast("&e" + p.getName() + " &7joined the lobby!");
	}

	@Override
	public void kickPlayer(ProxiedPlayer s, String name) {
		if (!s.getUniqueId().equals(host)) {
			Util.msg(s, "&cOnly the host may kick other players!");
			return;
		}
		// Lobby kicks are guaranteed to be online, since people who disconnect are auto-kicked from lobbies
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
		
		if (!players.contains(p.getUniqueId())) {
			Util.msg(s, "&cThat player isn't in your lobby!");
			return;
		}
		
		players.remove(p.getUniqueId());
		GameManager.removeFromSession(s.getUniqueId());
		broadcast("&e" + p.getName() + " &7was kicked from the lobby!");
	}
	
	@Override
	public void leavePlayer(ProxiedPlayer s) {
		if (s.getUniqueId().equals(host)) {
			GameManager.disbandLobby(this);
		}
		else {
			players.remove(s.getUniqueId());
			GameManager.removeFromSession(s.getUniqueId());
			broadcast("&e" + s.getName() + " &7left the lobby!");
		}
	}
	
	public void broadcast(String msg) {
		for (UUID uuid : players) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			Util.msg(p, msg);
		}
	}
	
	@Override
	public void displayInfo(ProxiedPlayer p) {
		game.getDescription()
	}
	
	public HashSet<UUID> getPlayers() {
		return players;
	}
	public HashSet<UUID> getInvited() {
		return invited;
	}
	public int getMinPlayers() {
		return minPlayers;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
