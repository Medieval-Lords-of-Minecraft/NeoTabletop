package me.neoblade298.neotabletop.commands;

import java.util.Map.Entry;
import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CmdTabletopInstances extends Subcommand {

	// /tt instances
	public CmdTabletopInstances(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSource s, String[] args) {
		if (GameManager.getInstances().size() == 0) {
			Util.msg(s, Component.text("There are currently no active instances!", NamedTextColor.RED));
			return;
		}
		ComponentBuilder b = SharedUtil.createText("&7List of instances (click to spectate):", null, null);
		for (Entry<String, GameInstance<? extends GamePlayer>> ent : GameManager.getInstances().entrySet()) {
			GameInstance<? extends GamePlayer> inst = ent.getValue();
			SharedUtil.appendText(b, "\n&7- &c" + inst.getName() + " &7(&6"
					+ inst.getGame().getName() + "&7)",
					"Click to spectate this game!",
					"/tt spectate " + inst.getName());
		}
		s.sendMessage(b.create());
	}
}
