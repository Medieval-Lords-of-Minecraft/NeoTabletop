package me.neoblade298.neotabletop;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class GameLobby<T extends GamePlayer> extends GameSession<T> {
	protected HashSet<UUID> players = new HashSet<UUID>(), invited = new HashSet<UUID>();

	public GameLobby(String name, Game game, UUID host, boolean isPublic) {
		super(name, game, host, isPublic);
		players.add(host);
	}
	
	public GameLobby(GameInstance<T> inst) {
		super(inst.getName(), inst.getGame(), inst.getHost(), inst.isPublic(), inst.getParameters());
	}

	public void startGame(Player s) {
		if (game.getMinPlayers() > players.size()) {
			Util.msgRaw(s, BungeeCore.miniMessage().deserialize("<red>You need at least <yellow>" + game.getMinPlayers() + "</yellow> players to start!"));
			return;
		}
		GameManager.startGame(this, onStart());
	}

	public abstract GameInstance<? extends GamePlayer> onStart();

	public void invitePlayer(Player inviter, String username) {
		if (!inviter.getUniqueId().equals(host)) {
			Util.displayError(inviter, "Only the host may invite other players!");
			return;
		}

		Optional<Player> recipient = BungeeCore.proxy().getPlayer(username);
		if (recipient.isEmpty()) {
			Util.displayError(inviter, "That player isn't online!");
			return;
		}

		invited.add(recipient.get().getUniqueId());
		broadcast("<yellow>" + recipient.get().getUsername() + " <gray>was invited to the lobby!");

		Util.msg(recipient.get(), BungeeCore.miniMessage()
				.deserialize("<gray>You've been invited to lobby <yellow>" + name + " </yellow>for <yellow>" + game.getName() + "</yellow>!"));
		
		
		Component c = Component.text().content("[").color(NamedTextColor.DARK_GRAY)
				.append(Component.text("Click here to accept the invite!", NamedTextColor.GREEN))
				.append(Component.text("]")).build();
		c = c.hoverEvent(HoverEvent.showText(Component.text("Click to accept invite")));
		c = c.clickEvent(ClickEvent.runCommand("/tt join " + name));
		recipient.get().sendMessage(c);
	}

	public void addPlayer(Player p) {
		if (!isPublic && !invited.contains(p.getUniqueId())) {
			Util.displayError(p, "You aren't invited to this private lobby!");
			return;
		}

		if (game.getMaxPlayers() <= players.size()) {
			Util.displayError(p, "This lobby is full as it has a max of " + game.getMaxPlayers() + " players!");
		}

		if (!isPublic) {
			invited.remove(p.getUniqueId());
		}

		GameManager.addToLobby(p, this);
		players.add(p.getUniqueId());
		displayInfo(p, p);
		broadcast("<yellow>" + p.getUsername() + " <gray>joined the lobby!");
	}
	
	public void addPlayerForce(Player p) {
		GameManager.addToLobby(p, this);
		players.add(p.getUniqueId());
	}

	@Override
	public void adminKickPlayer(CommandSource s, String name) {
		Player p = BungeeCore.proxy().getPlayer(name).get();
		players.remove(p.getUniqueId());
		GameManager.removeFromSession(p.getUniqueId());
		broadcast("<yellow>" + p.getUsername() + " <gray>was kicked from the lobby by an admin!");
	}

	@Override
	public void kickPlayer(Player s, String name) {
		if (!s.getUniqueId().equals(host)) {
			Util.displayError(s, "Only the host may kick other players!");
			return;
		}
		// Lobby kicks are guaranteed to be online, since people who disconnect are
		// auto-kicked from lobbies
		Optional<Player> opt = BungeeCore.proxy().getPlayer(name);

		if (opt.isEmpty() || !players.contains(opt.get().getUniqueId())) {
			Util.displayError(s, "That player isn't in your lobby!");
			return;
		}
		Player p = opt.get();

		players.remove(p.getUniqueId());
		GameManager.removeFromSession(p.getUniqueId());
		broadcast("<yellow>" + p.getUsername() + " <gray>was kicked from the lobby!");
	}

	@Override
	public void leavePlayer(Player s) {
		if (s.getUniqueId().equals(host)) {
			GameManager.disbandLobby(this);
		}
		else {
			players.remove(s.getUniqueId());
			GameManager.removeFromSession(s.getUniqueId());
			broadcast("<yellow>" + s.getUsername() + " <gray>left the lobby!");
		}
	}

	public void broadcast(String msg) {
		for (UUID uuid : players) {
			Optional<Player> p = BungeeCore.proxy().getPlayer(uuid);
			if (p.isPresent()) {
				Util.msgRaw(p.get(), BungeeCore.miniMessage().deserialize(msg));
			}
		}
	}

	@Override
	public void displayInfo(Player viewer, Player viewed) {
		Util.msgRaw(viewer, BungeeCore.miniMessage()
				.deserialize("<gray><< <red>" + name + " </red>(<gold>" + game.getName() + "</gold>) >>"));
		Optional<Player> h = BungeeCore.proxy().getPlayer(host);
		boolean isHost = h.isPresent() && viewer.getUniqueId().equals(host);
		Component c = BungeeCore.miniMessage()
				.deserialize("<dark_gray>[" + (isPublic ? "<green>" : "<gray>") + "Public <reset><dark_gray>| " + 
						(!isPublic ? "<green>" : "<gray>") + "Private<dark_gray>]");
		if (isHost) {
			c = c.hoverEvent(HoverEvent.showText(Component.text("Click to toggle!")));
			c = c.clickEvent(ClickEvent.runCommand(isPublic ? "/tt private" : "/tt public"));
		}
		
		// Public/Private
		viewer.sendMessage(c);
		
		// Params
		Util.msgRaw(viewer, Component.text("Parameters (Click to change):", NamedTextColor.GRAY));
		Builder b = Component.text();
		boolean first = true;
		for (GameParameter param : params.values()) {
			if (!first) {
				b.appendNewline();
			}
			first = false;
			c = SharedUtil.color("<gray>- <red>" + param.getName() + "</red>: <gold>" + param.get());
			c = c.hoverEvent(HoverEvent.showText(SharedUtil.color("<gray><i>" + param.getDescription() +
					"\n</i><white>Click to change parameter")));
			c = c.clickEvent(ClickEvent.suggestCommand("/tt set " + param.getKey() + " " + param.get()));
		}
		viewer.sendMessage(c);
		
		// Player list
		Util.msgRaw(viewer, Component.text("Players:", NamedTextColor.GRAY));
		Util.msgRaw(viewer, SharedUtil.color("<gray>- <red>" + h.get().getUsername() + " </red>(<yellow>Host</yellow>)"));
		b = Component.text();
		Component kick = SharedUtil.color("<dark_gray> [<red>Click to kick!</red>]");
		Component giveHost = SharedUtil.color("<dark_gray> [<red>Click to kick!</red>]");
		if (players.size() > 1) {
			first = true;
			for (UUID uuid : players) {
				if (uuid.equals(host)) continue;
				if (!first) {
					b.appendNewline();
				}
				first = false;
				Optional<Player> opt = BungeeCore.proxy().getPlayer(uuid);
				b.append(Component.text("- ", NamedTextColor.GRAY));
				if (viewer.getUniqueId().equals(host)) {
					kick = kick.hoverEvent(HoverEvent.showText(Component.text("Click to kick " + opt.get().getUsername())));
					kick = kick.clickEvent(ClickEvent.suggestCommand("/tt kick " + opt.get().getUsername()));
					
					giveHost = giveHost.hoverEvent(HoverEvent.showText(Component.text("Click to give host to " + opt.get().getUsername())));
					giveHost = giveHost.clickEvent(ClickEvent.suggestCommand("/tt sethost " + opt.get().getUsername()));
				}
			}
			viewer.sendMessage(c.append(kick).append(giveHost));
		}

		c = SharedUtil.color("<dark_gray>[<gray>Click here to read about the game!</gray>]");
		c = c.hoverEvent(HoverEvent.showText(Component.text("Click me!")));
		c = c.clickEvent(ClickEvent.suggestCommand("/tt viewgame " + game.getKey()));
		if (viewer.getUniqueId().equals(host)) {
			Component start = SharedUtil.color("<dark_gray>[<green>Click here to start!</green>] ");
			start = start.hoverEvent(HoverEvent.showText(Component.text("Click me to start!")));
			start = start.clickEvent(ClickEvent.runCommand("/tt start"));
			
			c = start.append(c);
		}
		viewer.sendMessage(c);
	}
	
	public boolean isFull() {
		return game.getMaxPlayers() > players.size();
	}

	public HashSet<UUID> getPlayers() {
		return players;
	}

	public HashSet<UUID> getInvited() {
		return invited;
	}
	
	public void setParameter(Player p, String param, String str) {
		if (!params.containsKey(param)) {
			Util.displayError(p, "That parameter doesn't exist!");
			return;
		}
		
		boolean success = params.get(param).set(str);
		if (success) {
			Util.msg(p, SharedUtil.color("<gray>Successfully set parameter <yellow>" + param + " </yellow>to <yellow>" + str));
			broadcast("The host has set parameter <yellow>" + param + " </yellow>to <yellow>" + str);
		}
		else {
			Util.displayError(p, "Failed to set parameter. Invalid value for parameter.");
		}
	}
}
