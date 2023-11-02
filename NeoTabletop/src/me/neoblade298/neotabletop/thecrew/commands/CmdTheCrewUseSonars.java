package me.neoblade298.neotabletop.thecrew.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class CmdTheCrewUseSonars extends Subcommand {

	// /thecrew usesonars
	public CmdTheCrewUseSonars(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		hidden = true;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof TheCrewInstance)) {
			Util.msgRaw(p, "&cYou're not in a game instance of The Crew!");
			return;
		}

		TheCrewInstance inst = (TheCrewInstance) sess;
		TheCrewPlayer tcp = inst.getPlayers().get(p.getUsername().toLowerCase());
		
		if (tcp.getSonarTokens() == 0) {
			Util.msg(s, "&cYou don't have any sonar tokens!");
			return;
		}
		tcp.displaySonarOptions();
	}

}
