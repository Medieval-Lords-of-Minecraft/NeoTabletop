package me.neoblade298.neotabletop.thecrew.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTheCrewMod extends Subcommand {

	// /tt kicklist
	public CmdTheCrewMod(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof GameInstance)) {
			Util.msgRaw(p, "&cYou're not in a game instance!");
			return;
		}
		
		if (!sess.getHost().equals(uuid)) {
			Util.msgRaw(p, "&cOnly the host may access the kick list!");
			return;
		}

		GameInstance<? extends GamePlayer> inst = (GameInstance<? extends GamePlayer>) sess;
		inst.displayKickList(s);
		ComponentBuilder b = SharedUtil.createText("&8[&cClick to redo round&8]", "Click here!", "/thecrew restartround");
		SharedUtil.appendText(b, " &8[&cClick to restart from round 1&8]", "Click here!", "/thecrew restartgame");
		SharedUtil.appendText(b, "\n&8[&cClick to return to lobby&8]", "Click here!", "/tt return");
		s.sendMessage(b.create());
	}

}
