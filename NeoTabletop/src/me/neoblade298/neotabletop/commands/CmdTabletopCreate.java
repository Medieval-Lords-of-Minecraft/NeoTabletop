package me.neoblade298.neotabletop.commands;

import java.util.ArrayList;
import java.util.UUID;

import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GameManager;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.GameSession;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.connection.Player;

public class CmdTabletopCreate extends Subcommand {

	// /tt create [game] [name] {public/private}
	public CmdTabletopCreate(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		Arg ga = new Arg("game");
		ga.setTabOptions(new ArrayList<String>(GameManager.getGames().keySet()));
		args.add(ga);
		args.add(new Arg("name"));
		args.add(new Arg("public/private", false));
	}

	@Override
	public void run(CommandSource s, String[] args) {
		String game = args[0].toLowerCase();
		Player p = (Player) s;
		UUID uuid = p.getUniqueId();
		boolean isPublic = false;
		
		if (args.length > 2) {
			if (args[2].equalsIgnoreCase("public")) {
				isPublic = true;
			}
		}
		
		// Check if the name exists already, or player is already in a game
		GameSession<? extends GamePlayer> sess = GameManager.getSession(uuid);
		if (sess != null) {
			String sessionType = sess instanceof GameLobby ? "lobby" : "instance";
			Util.msg(s, "&cYou're already in " + sessionType + " &e" + sess.getName() + " &7for game &e" + sess.getGame().getName() + "&7!");
			return;
		}
		else if (GameManager.sessionExists(args[1])) {
			Util.msg(s, "&cThe name &e" + args[1] + "&c is already taken!");
			return;
		}
		Game g = GameManager.getGame(game);
		if (g == null) {
			Util.msg(s, "&cThat game doesn't exist! Try using /tt games to see a full list!");
			return;
		}

		GameManager.createLobby(args[1], p, g, isPublic);
	}

}
