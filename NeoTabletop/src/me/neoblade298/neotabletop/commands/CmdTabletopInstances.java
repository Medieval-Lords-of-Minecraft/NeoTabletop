package me.neoblade298.neotabletop.commands;

import java.util.Map.Entry;
import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTabletopInstances extends Subcommand {

	// /tt instances
	public CmdTabletopInstances(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		ComponentBuilder b = SharedUtil.createText("&7List of instances:", null, null);
		for (Entry<String, GameInstance> ent : GameManager.getInstances().entrySet()) {
			GameInstance inst = ent.getValue();
			SharedUtil.appendText(b, "\n&7- &c" + inst.getName() + " &7(&6"
					+ inst.getGame().getName() + "&7)",
					"Click to spectate this game!",
					"tt spectate " + inst.getName());
		}
	}
}
