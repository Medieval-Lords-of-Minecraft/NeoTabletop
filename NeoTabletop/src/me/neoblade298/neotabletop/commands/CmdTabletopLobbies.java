package me.neoblade298.neotabletop.commands;

import java.util.Map.Entry;
import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopLobbies extends Subcommand {

	// /tt lobbies
	public CmdTabletopLobbies(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ComponentBuilder b = SharedUtil.createText("&7List of Lobbies:", null, null);
		for (Entry<String, GameLobby<? extends GamePlayer>> ent : GameManager.getLobbies().entrySet()) {
			GameLobby<? extends GamePlayer> lob = ent.getValue();
			SharedUtil.appendText(b, "\n&7- &c" + lob.getName() + " &7(&6"
					+ lob.getGame().getName() + "&7)",
					createHoverText(lob),
					lob.isPublic() && !lob.isFull() ? "tt join " + lob.getName() : null);
		}
	}

	private String createHoverText(GameLobby<? extends GamePlayer> lob) {
		String text = lob.isPublic() ? "&aPublic Lobby" + (lob.isFull() ? ", click to join!" : "") : "&cPrivate Lobby";
		
		String col = lob.isFull() ? "&a" : "&c";
		text += "\n&7Playercount: " + col + lob.getPlayers().size() + " &7/ " + lob.getGame().getMaxPlayers();
		
		ProxiedPlayer host = ProxyServer.getInstance().getPlayer(lob.getHost());
		text += "\n&7- &c" + host.getName() + " &7(&6Host&7)";
		
		for (UUID uuid : lob.getPlayers()) {
			if (uuid.equals(lob.getHost())) continue;
			
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			text += "\n&7- &c" + p.getName();
		}
		return text;
	}
}
