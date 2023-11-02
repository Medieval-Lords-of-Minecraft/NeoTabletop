package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTabletopSetParameter extends Subcommand {

	// /tt set [param] [value]
	public CmdTabletopSetParameter(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("parameter"));
		args.add(new Arg("value"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof GameLobby)) {
			Util.msg(s, Component.text("You're not in a game lobby!", NamedTextColor.RED));
			return;
		}
		GameLobby<? extends GamePlayer> lob = (GameLobby<? extends GamePlayer>) sess;
		
		if (!sess.getHost().equals(uuid)) {
			Util.msg(s, Component.text("Only the host may change the game parameters!", NamedTextColor.RED));
			return;
		}

		lob.setParameter(p, args[0], args[1]);
	}

}
