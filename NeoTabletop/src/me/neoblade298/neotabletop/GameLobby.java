package me.neoblade298.neotabletop;

import java.util.HashSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class GameLobby extends GameSession {
	protected HashSet<UUID> players = new HashSet<UUID>(), invited = new HashSet<UUID>();
	protected boolean isPublic;

	public GameLobby(String name, Game game, UUID host, boolean isPublic) {
		super(name, game, host);
		players.add(host);
		this.isPublic = isPublic;
	}

	public GameInstance startGame(ProxiedPlayer s) {
		if (!s.getUniqueId().equals(host)) {
			Util.msg(s, "&cOnly the host may start the game!");
			return null;
		}

		if (game.getMinPlayers() > players.size()) {
			Util.msg(s, "&cYou need at least &e" + game.getMinPlayers() + " &cplayers to start!");
			return null;
		}
		return onStart();
	}

	public abstract GameInstance onStart();

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
	public void displayInfo(ProxiedPlayer viewer) {
		Util.msg(viewer, "&7<< &c" + name + " &7(&6" + game.getName() + "&7) >>", false);
		ProxiedPlayer h = ProxyServer.getInstance().getPlayer(host);
		Util.msg(viewer, "&7- &c" + h.getName() + " &7(&eHost&7)", false);

		for (UUID uuid : players) {
			if (uuid.equals(host)) continue;
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			ComponentBuilder b = new ComponentBuilder("§7- §c" + p.getName());
			if (uuid.equals(host)) {
				SharedUtil.appendText(b, " &8[&cClick to kick&8]", "Click to kick " + p.getName(), "tt kick " + p.getName());
			}
			viewer.sendMessage(b.create());
		}

		viewer.sendMessage(SharedUtil.createText("&8[&7Click here to read about the game!&8]", "Click me!", "tt viewgame the_crew").create());
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

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
