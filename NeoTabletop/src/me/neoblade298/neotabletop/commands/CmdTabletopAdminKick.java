package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Player;

public class CmdTabletopAdminKick extends Subcommand {

	// /tta kick [player]
	public CmdTabletopAdminKick(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"));
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = ProxyServer.getInstance().getPlayer(args[0]);
		
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

		sess.adminKickPlayer(s, p.getUsername());
	}

}
