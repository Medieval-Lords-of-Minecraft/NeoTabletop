package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTabletopKick extends Subcommand {

	// /tt kick [player]
	public CmdTabletopKick(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null) {
			Util.msg(s, Component.text("You're not in a game session!", NamedTextColor.RED));
			return;
		}
		
		if (!sess.getHost().equals(uuid)) {
			Util.msg(s, Component.text("Only the host may change if this lobby is public or private!", NamedTextColor.RED));
			return;
		}

		sess.kickPlayer(p, args[0]);
	}

}
