package me.neoblade298.neotabletop.commands;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

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
			Util.msg(s, Component.text("You're already in a session! Use /tt leave", NamedTextColor.RED));
			return;
		}
		
		GameLobby<? extends GamePlayer> lob = GameManager.getLobby(args[0]);
		if (lob == null) {
			if (GameManager.getInstance(args[0]) != null) {
				Util.msg(s, Component.text("That lobby already started its game!", NamedTextColor.RED));
			}
			else {
				Util.msg(s, Component.text("That lobby doesn't exist!", NamedTextColor.RED));
			}
			return;
		}
		
		lob.addPlayer((Player) s);
	}

}
