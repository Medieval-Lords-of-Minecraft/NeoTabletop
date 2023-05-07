package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TabletopSpectate extends Subcommand {

	// /tt spectate [name]
	public TabletopSpectate(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("name"));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		if (GameManager.getSession(uuid) != null) {
			Util.msg(p, "&cYou're already in a game session!");
			return;
		}

		GameInstance inst = GameManager.getInstance(args[0]);
		if (inst == null) {
			Util.msg(p, "&cCould not find that game instance! Maybe the game hasn't started yet?");
			return;
		}
		
		inst.addSpectator(p);
	}

}
