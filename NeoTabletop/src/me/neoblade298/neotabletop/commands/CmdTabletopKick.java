package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopKick extends Subcommand {

	// /tt kick [player]
	public CmdTabletopKick(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null) {
			Util.msg(p, "&cYou're not in a game session!");
			return;
		}
		
		if (!sess.getHost().equals(uuid)) {
			Util.msg(p, "&cOnly the host may change whether a lobby is public or private!");
			return;
		}

		sess.kickPlayer(p, args[0]);
	}

}
