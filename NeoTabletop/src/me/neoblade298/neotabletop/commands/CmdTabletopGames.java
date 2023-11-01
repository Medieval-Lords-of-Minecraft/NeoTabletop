package me.neoblade298.neotabletop.commands;

import java.util.Map.Entry;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameManager;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CmdTabletopGames extends Subcommand {

	// /tt games
	public CmdTabletopGames(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSource s, String[] args) {
		ComponentBuilder b = SharedUtil.createText("&7List of games:", null, null);
		for (Entry<String, Game> ent : GameManager.getGames().entrySet()) {
			SharedUtil.appendText(b, "\n&7- &c" + ent.getKey(), "&6" + ent.getValue().getName() +
					"\n&fClick for more information!", "/tt viewgame " + ent.getValue().getKey());
		}
		s.sendMessage(b.create());
	}
}
