package me.neoblade298.neotabletop.thecrew.commands;

import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.SonarType;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.tasks.CardMatcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CmdTheCrewSonar extends Subcommand {

	// /thecrew sonar [card matcher] [min/max/only]
	public CmdTheCrewSonar(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("card matcher"));
		args.add(new Arg("sonar type"));
		hidden = true;
	}

	@Override
	public void run(CommandSender s, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) s;
		UUID uuid = p.getUniqueId();
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess == null || !(sess instanceof TheCrewInstance)) {
			Util.msgRaw(p, "&cYou're not in a game instance of The Crew!");
			return;
		}

		TheCrewInstance inst = (TheCrewInstance) sess;
		TheCrewPlayer tcp = inst.getPlayers().get(p.getName().toLowerCase());
		tcp.useSonarToken(new CardMatcher(args[0]), SonarType.valueOf(args[1]));
	}

}
