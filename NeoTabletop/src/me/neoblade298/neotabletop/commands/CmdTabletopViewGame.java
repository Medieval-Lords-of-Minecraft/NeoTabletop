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

public class CmdTabletopViewGame extends Subcommand {

	// /tt viewgame [game]
	public CmdTabletopViewGame(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("game"));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Game g = GameManager.getGame(args[0]);
		if (g == null) {
			Util.msg(s, "&cThat game doesn't exist! Try using /tt games to see a full list!");
			return;
		}

		g.displayInfo((ProxiedPlayer) s);
	}
}
