package me.neoblade298.neotabletop;

import java.util.logging.Level;

import me.neoblade298.neocore.bungee.commands.SubcommandManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.commands.*;
import me.neoblade298.neotabletop.thecrew.commands.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class NeoTabletop extends Plugin {
	private static NeoTabletop inst;
	
	public void onEnable() {
		inst = this;
		initCommands();
    	
    	// getProxy().getPluginManager().registerListener(this, new PaystubIO());
		getProxy().getPluginManager().registerListener(this, new GameManager());
	}
	
	private void initCommands() {
		SubcommandManager tt = new SubcommandManager("tt", "tabletop.use", ChatColor.RED, this);
		tt.registerCommandList("");
		tt.register(new CmdTabletopCreate("create", "Creates a lobby for a game", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopInvite("invite", "Invites players to your lobby", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopInfo("info", "View info of the player's current session", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopLeave("leave", "Leave your current session", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopLobbies("lobbies", "View a list of active lobbies", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopGames("games", "View a list of all playable games", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopInstances("instances", "View a list of all active games", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopJoin("join", "Join a lobby", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopKick("kick", "Kick a player from your session", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopKick("kicklist", "View moderation list", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopPublic("public", "Set your lobby to public", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopPrivate("private", "Set your lobby to private", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopStart("start", "Starts the game", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopSpectate("spectate", "Spectate an active game", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopSetParameter("set", "Sets a game parameter", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopSetHost("sethost", "Sets the game host", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopViewGame("viewgame", "Gets additional info for a game", null, SubcommandRunner.PLAYER_ONLY));
		tt.register(new CmdTabletopManual("manual", "View a game's manual", null, SubcommandRunner.PLAYER_ONLY));
		
		SubcommandManager tta = new SubcommandManager("tta", "tabletop.admin", ChatColor.DARK_RED, this);
		tta.registerCommandList("");
		tta.register(new CmdTabletopAdminKick("kick", "Force kicks a player from a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminSetHost("sethost", "Force sets a host for a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminDisband("end", "Force sets a host for a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminDebug("debug", "Show debug for an instance", null, SubcommandRunner.PLAYER_ONLY));

		SubcommandManager thecrew = new SubcommandManager("thecrew", "tabletop.use", ChatColor.RED, this);
		thecrew.register(new CmdTheCrewPlay("play", "Plays a card in your hand", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewAcceptTasks("accepttasks", "Accepts rolled tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewAcceptTask("accepttask", "Accepts a task", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewPassTask("passtask", "Passes on accepting a task", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewRerollTasks("rerolltasks", "Rerolls tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewViewTasks("viewtasks", "Views assigned tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewViewHand("viewhand", "Views a player's hand", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewMod("mod", "Views moderation list for The Crew", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewTest("test", "Tests runtimes", "tabletop.admin", SubcommandRunner.BOTH));
	}
	
	public static NeoTabletop inst() {
		return inst;
	}
	
	public static TaskScheduler scheduler() {
		return inst.getProxy().getScheduler();
	}
	
	public static ProxyServer proxy() {
		return inst.getProxy();
	}
}
