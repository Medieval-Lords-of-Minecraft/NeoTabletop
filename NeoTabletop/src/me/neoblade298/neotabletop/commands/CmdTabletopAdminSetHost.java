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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopAdminSetHost extends Subcommand {

	// /tta sethost [player]
	public CmdTabletopAdminSetHost(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
		
		if (p == null) {
			Util.msg(s, "&cThat player isn't online!");
			return;
		}
		
		// Check if the name exists already, or player is already in a game
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null) {
			Util.msg(s, "&cThat player isn't in a game session!");
			return;
		}
		
		if (sess.getHost().equals(p.getUniqueId())) {
			Util.msg(s, "&cThat player is already the host!");
		}

		sess.setHost(s, p.getName());
	}

}
