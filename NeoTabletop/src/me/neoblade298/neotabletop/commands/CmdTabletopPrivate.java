package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopPrivate extends Subcommand {

	// /tt public
	public CmdTabletopPrivate(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof GameLobby)) {
			Util.msg(p, "&cYou're not in a lobby!");
			return;
		}
		
		if (!sess.getHost().equals(uuid)) {
			Util.msg(p, "&cOnly the host may change whether a lobby is public or private!");
			return;
		}

		((GameLobby<? extends GamePlayer>) sess).setPublic(false);
		Util.msg(p, "Successfully set lobby to private!");
	}

}
