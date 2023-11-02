package me.neoblade298.neotabletop.commands;

import com.velocitypowered.api.command.CommandSource;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdTabletopAdminDisband extends Subcommand {

	// /tta disband [name]
	public CmdTabletopAdminDisband(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("name"));
	}

	@Override
	public void run(CommandSource s, String[] args) {
		GameSession<? extends GamePlayer> sess = GameManager.getSession(args[0]);
		if (sess == null) {
			Util.msg(s, Component.text("That instance doesn't exist!", NamedTextColor.RED));
			return;
		}

		GameManager.disbandSession(sess);
	}

}
