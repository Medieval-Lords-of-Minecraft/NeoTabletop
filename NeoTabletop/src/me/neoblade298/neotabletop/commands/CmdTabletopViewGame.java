package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTabletopViewGame extends Subcommand {

	// /tt viewgame [game]
	public CmdTabletopViewGame(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("game"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Game g = GameManager.getGame(args[0]);
		if (g == null) {
			Util.msg(s, Component.text("That game doesn't exist! Try using /tt games", NamedTextColor.RED));
			return;
		}

		g.displayInfo((Player) s);
	}
}
