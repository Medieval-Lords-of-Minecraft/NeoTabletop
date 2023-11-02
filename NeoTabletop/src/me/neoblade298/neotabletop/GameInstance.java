package me.neoblade298.neotabletop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class GameInstance<T extends GamePlayer> extends GameSession<T> {
	protected HashMap<String, T> players = new HashMap<String, T>();
	protected HashSet<Player> spectators = new HashSet<Player>();
	public GameInstance(GameLobby<T> lobby) {
		super(lobby.getName(), lobby.getGame(), lobby.getHost(), lobby.isPublic(), lobby.getParameters());
	}
	
	public abstract void handleLeave(GamePlayer gp);

	@Override
	public void adminKickPlayer(CommandSource s, String name) {
		Player p = BungeeCore.proxy().getPlayer(name).get();
		GamePlayer gp = players.remove(name.toLowerCase());
		GameManager.removeFromSession(gp.getUniqueId());
		broadcast("&e" + p.getUsername() + " &7was kicked from the game by an admin!");
		handleLeave(gp);
	}
	
	@Override
	public void kickPlayer(Player p, String name) {
		if (!p.getUniqueId().equals(host)) {
			Util.displayError(p, "Only the host may kick other players!");
			return;
		}
		
		if (!players.containsKey(name.toLowerCase())) {
			Util.displayError(p, "That player isn't in your game!");
			return;
		}
		
		GamePlayer gp = players.remove(name.toLowerCase());
		GameManager.removeFromSession(gp.getUniqueId());
		broadcast("&e" + p.getUsername() + " &7was kicked from the game!");
		handleLeave(gp);
	}

	@Override
	public void broadcast(String msg) {
		for (GamePlayer gp : players.values()) {
			Util.msgRaw(gp.getPlayer(), BungeeCore.miniMessage().deserialize(msg).colorIfAbsent(NamedTextColor.GRAY));
		}
		for (Player p : spectators) {
			Util.msgRaw(p, BungeeCore.miniMessage().deserialize(msg).colorIfAbsent(NamedTextColor.GRAY));
		}
	}
	
	public void broadcast(Component bc) {
		for (GamePlayer gp : players.values()) {
			gp.getPlayer().sendMessage(bc);
		}
		for (Player p : spectators) {
			p.sendMessage(bc);
		}
	}

	@Override
	public void leavePlayer(Player p) {
		if (players.containsKey(p.getUsername().toLowerCase())) {
			GamePlayer gp = players.remove(p.getUsername().toLowerCase());
			GameManager.removeFromSession(p.getUniqueId());
			broadcast("&e" + p.getUsername() + " &7left the lobby!");
			
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
			broadcast("&e" + p.getUsername() + " &7stopped spectating!");
		}
		else {
			Util.displayError(p, "Something went wrong! You were unable to leave game &e" + name + "&c.");
			return;
		}
	}
	
	public HashMap<String, T> getPlayers() {
		return players;
	}
	
	public void addSpectator(Player p) {
		spectators.add(p);
		broadcast("&e" + p.getUsername() + " &7began spectating!");
		onSpectate(p);
	}
	
	public void endGame() {
		GameManager.endGame(onEnd(), this);
	}
	
	public void displayKickList(CommandSource cmdUser) {
		Player p = (Player) cmdUser;
		Util.msgRaw(cmdUser, Component.text("Players:", NamedTextColor.GRAY));
		Component c = Component.text().content("- ").color(NamedTextColor.GRAY)
				.append(Component.text(p.getUsername(), NamedTextColor.RED))
				.append(Component.text(" ("))
				.append(Component.text("Host", NamedTextColor.YELLOW))
				.append(Component.text(")")).build();
		p.sendMessage(c);
		boolean first = true;
		Builder b = Component.text();
		for (GamePlayer gp : players.values()) {
			UUID uuid = gp.getUniqueId();
			if (uuid.equals(host)) continue;
			if (!first) {
				b.appendNewline();
			}
			first = false;
			
			b.append(Component.text("- ", NamedTextColor.GRAY)).append(Component.text(gp.getName(), NamedTextColor.RED));
			b.appendNewline();
			
			Component kick = Component.text().content(" [").color(NamedTextColor.DARK_GRAY)
					.append(Component.text("Click to kick", NamedTextColor.RED))
					.append(Component.text("]")).build();
			kick.hoverEvent(HoverEvent.showText(Component.text("Click to kick " + gp.getName())));
			kick.clickEvent(ClickEvent.runCommand("/tt kick " + gp.getName()));
			Component giveHost = Component.text().content(" [").color(NamedTextColor.DARK_GRAY)
					.append(Component.text("Click to give host", NamedTextColor.RED))
					.append(Component.text("]")).build();
			kick.hoverEvent(HoverEvent.showText(Component.text("Click to give host to " + gp.getName())));
			kick.clickEvent(ClickEvent.runCommand("/tt sethost " + gp.getName()));
			
			cmdUser.sendMessage(b.append(kick).append(giveHost).build());
		}
	}
	
	public abstract void onSpectate(Player p);
	public abstract void showDebug(CommandSource s);
	public abstract GameLobby<T> onEnd();
}
