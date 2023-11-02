package me.neoblade298.neotabletop.commands;

import com.velocitypowered.api.command.CommandSource;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdTabletopAdminDebug extends Subcommand {

	// /tt debug [instance name]
	public CmdTabletopAdminDebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("session name"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		GameInstance<? extends GamePlayer> inst = GameManager.getInstance(args[0]);
		if (inst == null) {
			Util.msg(s, Component.text("That instance doesn't exist!", NamedTextColor.RED));
			return;
		}

		inst.showDebug(s);
	}

}
