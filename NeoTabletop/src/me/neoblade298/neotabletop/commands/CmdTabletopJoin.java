package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopJoin extends Subcommand {

	// /tt join [name]
	public CmdTabletopJoin(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("name"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		GameLobby lob = GameManager.getLobby(args[0]);
		if (lob == null) {
			if (GameManager.getInstance(args[0]) != null) {
				Util.msg(s, "&cThat lobby already started its game!");
			}
			else {
				Util.msg(s, "&cThat lobby doesn't exist!");
			}
			return;
		}
		
		lob.addPlayer((ProxiedPlayer) s);
	}

}
