package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrew;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GameManager implements Listener {
	private static HashMap<String, GameLobby> lobbies = new HashMap<String, GameLobby>();
	private static HashMap<String, GameInstance> instances = new HashMap<String, GameInstance>();
	private static HashMap<UUID, GameSession> inSession = new HashMap<UUID, GameSession>(); // Can be lobby or instance
	private static HashMap<String, Game> games = new HashMap<String, Game>();
	
	public GameManager() {
		// Creating instance of game registers it to games via Game.java
		new TheCrew();
	}
	
	// Kick player out of lobbies on disconnect, disband lobby if host is kicked
    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
    	ProxiedPlayer p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	if (inSession.containsKey(uuid)) {
    		GameSession sess = inSession.get(uuid);
    		if (sess instanceof GameLobby) {
    			GameLobby lob = (GameLobby) sess;
    			inSession.remove(uuid);
    			if (lob.getHost().equals(uuid)) {
    				disbandLobby(lob);
    			}
    			else {
    				lob.leavePlayer(p);
    			}
    		}
    	}
    }
    
    public static GameSession getSession(UUID uuid) {
    	return inSession.get(uuid);
    }
    
    public static GameLobby getLobby(String name) {
    	return lobbies.get(name.toLowerCase());
    }
    
    public static GameInstance getInstance(String name) {
    	return instances.get(name.toLowerCase());
    }
    
    // Creates a lobby
	public static void createLobby(String name, ProxiedPlayer sender, Game game, boolean isPublic) {
		UUID uuid = sender.getUniqueId();
		GameLobby lobby = game.createLobby(name, uuid, isPublic);
		lobbies.put(name.toLowerCase(), lobby);
		inSession.put(sender.getUniqueId(), lobby);
		Util.msg(sender, "Successfully created lobby &e" + lobby.getName() + "&7!");
	}
    
	public static void disbandLobby(GameLobby lob) {
		for (UUID uuid : lob.getPlayers()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			inSession.remove(p.getUniqueId());
		}
		lob.broadcast("Due to the host leaving, lobby &e" + lob.getName() + " &7was disbanded.");
		lobbies.remove(lob.getName().toLowerCase());
	}
	
	public static void disbandInstance(GameInstance inst, String reason) {
		for (GamePlayer gp : inst.getPlayers().values()) {
			inSession.remove(gp.getUniqueId());
		}
		inst.broadcast("Due to " + reason + ", instance &e" + inst.getName() + " &7was disbanded.");
		instances.remove(inst.getName().toLowerCase());
	}
	
	public static void removeFromSession(UUID uuid) {
		inSession.remove(uuid);
	}
	
	public static boolean sessionExists(String name) {
		return lobbies.containsKey(name.toLowerCase()) || instances.containsKey(name.toLowerCase());
	}
	
	public static HashMap<String, GameLobby> getLobbies() {
		return lobbies;
	}
	
	public static HashMap<String, GameInstance> getInstances() {
		return instances;
	}
	
	public static Game getGame(String name) {
		return games.get(name);
	}
	
	public static HashMap<String, Game> getGames() {
		return games;
	}
	
	public static void registerGame(Game g) {
		games.put(g.getKey(), g);
	}
}
