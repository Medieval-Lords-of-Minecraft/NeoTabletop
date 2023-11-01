package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.connection.Player;

public class CmdTabletopJoin extends Subcommand {

	// /tt join [name]
	public CmdTabletopJoin(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("name"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		if (GameManager.getSession(p.getUniqueId()) != null) {
			Util.msg(p, "&cYou're already in a session! Use /tt leave!");
			return;
		}
		
		GameLobby<? extends GamePlayer> lob = GameManager.getLobby(args[0]);
		if (lob == null) {
			if (GameManager.getInstance(args[0]) != null) {
				Util.msg(s, "&cThat lobby already started its game!");
			}
			else {
				Util.msg(s, "&cThat lobby doesn't exist!");
			}
			return;
		}
		
		lob.addPlayer((Player) s);
	}

}
