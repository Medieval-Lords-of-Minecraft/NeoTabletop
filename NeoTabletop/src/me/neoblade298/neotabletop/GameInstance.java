package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameInstance<T extends GamePlayer> extends GameSession<T> {
	protected HashMap<String, T> players = new HashMap<String, T>();
	protected HashSet<ProxiedPlayer> spectators = new HashSet<ProxiedPlayer>();
	public GameInstance(GameLobby<T> lobby) {
		super(lobby.getName(), lobby.getGame(), lobby.getHost(), lobby.isPublic(), lobby.getParameters());
	}
	
	public abstract void handleLeave(GamePlayer gp);

	@Override
	public void adminKickPlayer(CommandSender s, String name) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
		GamePlayer gp = players.remove(name.toLowerCase());
		GameManager.removeFromSession(gp.getUniqueId());
		broadcast("&e" + p.getName() + " &7was kicked from the game by an admin!");
		handleLeave(gp);
	}
	
	@Override
	public void kickPlayer(ProxiedPlayer p, String name) {
		if (!p.getUniqueId().equals(host)) {
			Util.msgRaw(p, "&cOnly the host may kick other players!");
			return;
		}
		
		if (!players.containsKey(name.toLowerCase())) {
			Util.msgRaw(p, "&cThat player isn't in your game!");
			return;
		}
		
		GamePlayer gp = players.remove(name.toLowerCase());
		GameManager.removeFromSession(gp.getUniqueId());
		broadcast("&e" + p.getName() + " &7was kicked from the game!");
		handleLeave(gp);
	}

	@Override
	public void broadcast(String msg) {
		for (GamePlayer gp : players.values()) {
			Util.msgRaw(gp.getPlayer(), msg);
		}
		for (ProxiedPlayer p : spectators) {
			Util.msgRaw(p, msg);
		}
	}
	
	public void broadcast(BaseComponent[] bc) {
		for (GamePlayer gp : players.values()) {
			gp.getPlayer().sendMessage(bc);
		}
		for (ProxiedPlayer p : spectators) {
			p.sendMessage(bc);
		}
	}

	@Override
	public void leavePlayer(ProxiedPlayer p) {
		if (players.containsKey(p.getName().toLowerCase())) {
			GamePlayer gp = players.remove(p.getName().toLowerCase());
			GameManager.removeFromSession(p.getUniqueId());
			broadcast("&e" + p.getName() + " &7left the lobby!");
			
			if (p.getUniqueId().equals(host)) {
				if (players.size() != 0) {
					GamePlayer next = players.values().iterator().next();
					host = next.getUniqueId();
					broadcast("&7Because the host left, the new host is now &e" + next.getName() + "&7!");
				}
				else {
					
				}
			}
			handleLeave(gp);
		}
		else if (spectators.contains(p)) {
			spectators.remove(p);
			GameManager.removeFromSession(p.getUniqueId());
			broadcast("&e" + p.getName() + " &7stopped spectating!");
		}
		else {
			Util.msgRaw(p, "&cSomething went wrong! You were unable to leave game &e" + name + "&c.");
			return;
		}
	}
	
	public HashMap<String, T> getPlayers() {
		return players;
	}
	
	public void addSpectator(ProxiedPlayer p) {
		spectators.add(p);
		broadcast("&e" + p.getName() + " &7began spectating!");
		onSpectate(p);
	}
	
	public void endGame() {
		GameManager.endGame(onEnd(), this);
	}
	
	public void displayKickList(CommandSender cmdUser) {
		Util.msgRaw(cmdUser, "&7Players:");
		Util.msgRaw(cmdUser, "&7- &c" + cmdUser.getName() + " &7(&eHost&7)");
		ComponentBuilder b = new ComponentBuilder();
		
		boolean first = true;
		for (GamePlayer gp : players.values()) {
			UUID uuid = gp.getUniqueId();
			if (uuid.equals(host)) continue;
			if (!first) {
				SharedUtil.appendText(b, "\n");
			}
			first = false;
			
			SharedUtil.appendText(b, "&7- &c" + gp.getName());
			SharedUtil.appendText(b, " &8[&cClick to kick&8]", "Click to kick " + gp.getName(), "/tt kick " + gp.getName());
			SharedUtil.appendText(b, " &8[&cClick to give host&8]", "Click to give host to " + gp.getName(), "/tt sethost " + gp.getName());
			cmdUser.sendMessage(b.create());
		}
	}
	
	public abstract void onSpectate(ProxiedPlayer p);
	public abstract void showDebug(CommandSender s);
	public abstract GameLobby<T> onEnd();
}
