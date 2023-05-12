package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.md_5.bungee.api.CommandSender;

public class CmdTabletopDebug extends Subcommand {

	// /tt debug [instance name]
	public CmdTabletopDebug(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("session name"));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		GameInstance<? extends GamePlayer> inst = GameManager.getInstance(args[0]);
		if (inst == null) {
			Util.msg(s, "&cThat instance doesn't exist!");
			return;
		}

		inst.showDebug(s);
	}

}
