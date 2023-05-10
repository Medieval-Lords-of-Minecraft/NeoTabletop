package me.neoblade298.neotabletop;

import java.util.HashSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameLobby<T extends GamePlayer> extends GameSession<T> {
	protected HashSet<UUID> players = new HashSet<UUID>(), invited = new HashSet<UUID>();

	public GameLobby(String name, Game game, UUID host, boolean isPublic) {
		super(name, game, host, isPublic);
		players.add(host);
	}
	
	public GameLobby(GameInstance<T> inst) {
		super(inst.getName(), inst.getGame(), inst.getHost(), inst.isPublic(), inst.getParameters());
	}

	public void startGame(ProxiedPlayer s) {
		if (game.getMinPlayers() > players.size()) {
			Util.msg(s, "&cYou need at least &e" + game.getMinPlayers() + " &cplayers to start!");
			return;
		}
		GameManager.startGame(this, onStart());
	}

	public abstract GameInstance<? extends GamePlayer> onStart();

	public void invitePlayer(ProxiedPlayer inviter, String username) {
		if (!inviter.getUniqueId().equals(host)) {
			Util.msg(inviter, "&cOnly the host may invite other players!");
			return;
		}

		ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(username);
		if (recipient == null) {
			Util.msg(inviter, "&cThat player isn't online!");
			return;
		}

		invited.add(recipient.getUniqueId());
		broadcast("&e" + recipient.getName() + " &7was invited to the lobby!");

		Util.msg(recipient, "You've been invited to lobby &e" + name + " &7for &e" + game.getName() + "&7!");
		
		inviter.sendMessage(SharedUtil.createText("&8[&aClick here to accept the invite!&8]", "Click to accept invite", "tt join " + name).create());
	}

	public void addPlayer(ProxiedPlayer p) {
		if (!isPublic && invited.contains(p.getUniqueId())) {
			Util.msg(p, "&cYou aren't invited to this private lobby!");
			return;
		}

		if (game.getMaxPlayers() <= players.size()) {
			Util.msg(p, "&cThis lobby is full as it has a max of &e" + game.getMaxPlayers() + " &cplayers!");
		}

		if (!isPublic) {
			invited.remove(p.getUniqueId());
		}

		players.add(p.getUniqueId());
		broadcast("&e" + p.getName() + " &7joined the lobby!");
	}

	@Override
	public void adminKickPlayer(CommandSender s, String name) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
		players.remove(p.getUniqueId());
		GameManager.removeFromSession(p.getUniqueId());
		broadcast("&e" + p.getName() + " &7was kicked from the lobby by an admin!");
	}

	@Override
	public void kickPlayer(ProxiedPlayer s, String name) {
		if (!s.getUniqueId().equals(host)) {
			Util.msg(s, "&cOnly the host may kick other players!");
			return;
		}
		// Lobby kicks are guaranteed to be online, since people who disconnect are
		// auto-kicked from lobbies
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);

		if (!players.contains(p.getUniqueId())) {
			Util.msg(s, "&cThat player isn't in your lobby!");
			return;
		}

		players.remove(p.getUniqueId());
		GameManager.removeFromSession(p.getUniqueId());
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
	public void displayInfo(ProxiedPlayer viewer, ProxiedPlayer viewed) {
		Util.msg(viewer, "&7<< &c" + name + " &7(&6" + game.getName() + "&7) >>", false);
		ProxiedPlayer h = ProxyServer.getInstance().getPlayer(host);
		boolean isHost = viewer.getUniqueId().equals(host);
		ComponentBuilder b = new ComponentBuilder();
		
		// Public/Private
		SharedUtil.appendText(b, "&8[" + (isPublic ? "&a" : "&7") + "Public &8| " + (!isPublic ? "&a" : "&7") + "Private&8]",
				isHost ? "Click to toggle!" : null,
				isHost ? (isPublic ? "tt private" : "tt public") : null);
		viewer.sendMessage(b.create());
		
		// Params
		Util.msg(viewer, "&7Parameters (Click one to change):", false);
		b = new ComponentBuilder();
		for (GameParameter param : params.values()) {
			SharedUtil.appendText(b, "&7- &c" + param.getName() + "&7: &6" + param.get(), "Click to change parameter",
					"tt set " + param.getKey() + " " + param.get(), ClickEvent.Action.SUGGEST_COMMAND);
		}
		viewer.sendMessage(b.create());
		
		// Player list
		Util.msg(viewer, "&7Players:");
		Util.msg(viewer, "&7- &c" + h.getName() + " &7(&eHost&7)", false);
		b = new ComponentBuilder();
		for (UUID uuid : players) {
			if (uuid.equals(host)) continue;
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			SharedUtil.appendText(b, "\n&7- &c" + p.getName());
			if (viewer.getUniqueId().equals(host)) {
				SharedUtil.appendText(b, " &8[&cClick to kick&8]", "Click to kick " + p.getName(), "tt kick " + p.getName());
				SharedUtil.appendText(b, " &8[&cClick to give host&8]", "Click to give host to " + p.getName(), "tt sethost " + p.getName());
			}
			viewer.sendMessage(b.create());
		}

		b = new ComponentBuilder();
		SharedUtil.appendText(b, "&8[&aClick here to start!&8]", "Click me to start!", "tt start");
		SharedUtil.appendText(b, "&8[&7Click here to read about the game!&8]", "Click me!", "tt viewgame " + game.getKey());
		viewer.sendMessage(b.create());
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
	
	public void setParameter(ProxiedPlayer p, String param, String str) {
		if (!params.containsKey(param)) {
			Util.msg(p, "&cThat parameter doesn't exist!");
			return;
		}
		
		boolean success = params.get(param).set(str);
		if (success) {
			Util.msg(p, "Successfully set parameter &e" + param + " &7to &e" + str);
		}
		else {
			Util.msg(p, "&cFailed to set parameter. Invalid value for parameter.");
		}
	}
}
