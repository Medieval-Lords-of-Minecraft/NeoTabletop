package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopManual extends Subcommand {

	// /tt manual [game] {page}
	public CmdTabletopManual(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("game"));
		args.add(new Arg("page", false));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Game g = GameManager.getGame(args[0]);
		if (g == null) {
			Util.msg(s, "&cThat game doesn't exist! Try using /tt games to see a full list!");
			return;
		}

		if (args.length > 1) {
			if (!SharedUtil.isNumeric(args[1])) {
				Util.msg(s, "&cPage number must be an integer!");
				return;
			}
			g.displayManual((ProxiedPlayer) s, Integer.parseInt(args[1]));
		}
		else {
			g.displayManual((ProxiedPlayer) s);
		}
	}
}
