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
	private static HashMap<String, GameLobby<? extends GamePlayer>> lobbies = new HashMap<String, GameLobby<? extends GamePlayer>>();
	private static HashMap<String, GameInstance<? extends GamePlayer>> instances = new HashMap<String, GameInstance<? extends GamePlayer>>();
	private static HashMap<UUID, GameSession<? extends GamePlayer>> inSession = new HashMap<UUID, GameSession<? extends GamePlayer>>(); // Can be lobby or instance
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
    		GameSession<? extends GamePlayer> sess = inSession.get(uuid);
    		if (sess instanceof GameLobby) {
    			GameLobby<? extends GamePlayer> lob = (GameLobby<? extends GamePlayer>) sess;
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
    
    public static GameSession<? extends GamePlayer> getSession(String name) {
    	if (lobbies.containsKey(name)) return lobbies.get(name);
    	return instances.get(name);
    }
    
    public static GameSession<? extends GamePlayer> getSession(UUID uuid) {
    	return inSession.get(uuid);
    }
    
    public static GameLobby<? extends GamePlayer> getLobby(String name) {
    	return lobbies.get(name.toLowerCase());
    }
    
    public static GameInstance<? extends GamePlayer> getInstance(String name) {
    	return instances.get(name.toLowerCase());
    }
    
    // Creates a lobby
	public static void createLobby(String name, ProxiedPlayer sender, Game game, boolean isPublic) {
		UUID uuid = sender.getUniqueId();
		GameLobby<? extends GamePlayer> lobby = game.createLobby(name, uuid, isPublic);
		lobbies.put(name.toLowerCase(), lobby);
		inSession.put(sender.getUniqueId(), lobby);
		Util.msg(sender, "Successfully created lobby &e" + lobby.getName() + "&7!");
		lobby.displayInfo(sender, sender);
	}
	
	public static void disbandSession(GameSession<? extends GamePlayer> sess) {
		if (sess instanceof GameLobby) {
			disbandLobby((GameLobby<? extends GamePlayer>) sess);
		}
		else if (sess instanceof GameInstance) {
			disbandInstance((GameInstance<? extends GamePlayer>) sess, "admin override");
		}
	}
    
	public static void disbandLobby(GameLobby<? extends GamePlayer> lob) {
		for (UUID uuid : lob.getPlayers()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			inSession.remove(p.getUniqueId());
		}
		lob.broadcast("&4[&4&lMLMC&c] &7Due to the host leaving, lobby &e" + lob.getName() + " &7was disbanded.");
		lobbies.remove(lob.getName().toLowerCase());
	}
	
	public static void disbandInstance(GameInstance<? extends GamePlayer> inst, String reason) {
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
	
	public static HashMap<String, GameLobby<? extends GamePlayer>> getLobbies() {
		return lobbies;
	}
	
	public static HashMap<String, GameInstance<? extends GamePlayer>> getInstances() {
		return instances;
	}
	
	public static void addToLobby(ProxiedPlayer p, GameLobby lob) {
		inSession.put(p.getUniqueId(), lob);
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
	
	public static void startGame(GameLobby<? extends GamePlayer> lob, GameInstance<? extends GamePlayer> inst) {
		lob.broadcast("The game has started!");

		lobbies.remove(lob.getName());
		instances.put(inst.getName(), inst);
		for (UUID uuid : lob.getPlayers()) {
			inSession.put(uuid, inst);
		}
	}
	
	public static void endGame(GameLobby<? extends GamePlayer> lob, GameInstance<? extends GamePlayer> inst) {
		inst.broadcast("The game has ended and you've all been returned to the lobby.");
		instances.remove(inst.getName());
		lobbies.put(lob.getName(), lob);
		for (GamePlayer gp : inst.getPlayers().values()) {
			inSession.put(gp.getUniqueId(), lob);
			lob.displayInfo(gp.getPlayer(), gp.getPlayer());
		}
	}
}
