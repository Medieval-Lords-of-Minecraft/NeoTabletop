package me.neoblade298.neotabletop.commands;

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

public class CmdTabletopInfo extends Subcommand {

	// /tt info {player}
	public CmdTabletopInfo(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = args.length > 0 ? ProxyServer.getInstance().getPlayer(args[0]) : (ProxiedPlayer) s;
		GameSession<? extends GamePlayer> sess = GameManager.getSession(p.getUniqueId());
		if (sess == null) {
			Util.msgRaw(p, "&cPlayer is not currently in a session!");
			return;
		}

		sess.displayInfo((ProxiedPlayer) s, p);
	}

}
