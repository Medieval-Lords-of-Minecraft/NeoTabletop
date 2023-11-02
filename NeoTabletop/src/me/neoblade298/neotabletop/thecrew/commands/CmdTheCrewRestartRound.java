package me.neoblade298.neotabletop.thecrew.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTheCrewRestartRound extends Subcommand {

	public CmdTheCrewRestartRound(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof TheCrewInstance)) {
			Util.msgRaw(p, "&cYou're not in a game of The Crew!");
			return;
		}
		
		if (!sess.getHost().equals(uuid)) {
			Util.msgRaw(p, "&cOnly the host may restart the round!");
			return;
		}

		TheCrewInstance inst = (TheCrewInstance) sess;
		inst.restartRound(p);
	}

}
