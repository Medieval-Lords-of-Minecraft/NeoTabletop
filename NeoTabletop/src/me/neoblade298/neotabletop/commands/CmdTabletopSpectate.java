package me.neoblade298.neotabletop.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTabletopSpectate extends Subcommand {

	// /tt spectate [name]
	public CmdTabletopSpectate(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("name"));
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		if (GameManager.getSession(uuid) != null) {
			Util.msg(s, Component.text("You're already in a session! Use /tt leave", NamedTextColor.RED));
			return;
		}

		GameInstance<? extends GamePlayer> inst = GameManager.getInstance(args[0]);
		if (inst == null) {
			Util.msg(s, Component.text("Couldn't find that game instance! Maybe the game is still in lobby phase?", NamedTextColor.RED));
			return;
		}
		
		inst.addSpectator(p);
	}

}
