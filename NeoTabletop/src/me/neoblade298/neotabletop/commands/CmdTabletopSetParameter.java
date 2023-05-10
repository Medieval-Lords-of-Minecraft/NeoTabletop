package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopSetParameter extends Subcommand {

	// /tt set [param] [value]
	public CmdTabletopSetParameter(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("parameter"));
		args.add(new Arg("value"));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof GameLobby)) {
			Util.msg(p, "&cYou're not in a game lobby!");
			return;
		}
		GameLobby<? extends GamePlayer> lob = (GameLobby<? extends GamePlayer>) sess;
		
		if (!sess.getHost().equals(uuid)) {
			Util.msg(p, "&cOnly the host may change game parameters!");
			return;
		}

		lob.setParameter(p, args[0], args[1]);
	}

}
