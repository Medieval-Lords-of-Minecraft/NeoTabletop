package me.neoblade298.neotabletop;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.commands.SubcommandManager;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.commands.*;
import me.neoblade298.neotabletop.thecrew.commands.*;
import net.kyori.adventure.text.format.NamedTextColor;

@Plugin(id = "neotabletop", name = "NeoTabletop", version = "0.1.0-SNAPSHOT",
        url = "https://ml-mc.com", description = "Neo's tabletop games plugin", authors = {"Ascheladd"})
public class NeoTabletop {
	private static NeoTabletop inst;
	
	public void onEnable() {
		inst = this;
		initCommands();
    	
    	// getProxy().getPluginManager().registerListener(this, new PaystubIO());
		BungeeCore.proxy().getEventManager().register(this, new GameManager());
	}
	
	private void initCommands() {
		CommandManager mngr = BungeeCore.proxy().getCommandManager();
		SubcommandManager tt = new SubcommandManager("tt", "tabletop.use", NamedTextColor.RED, mngr, this);
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
		tt.register(new CmdTabletopReturn("return", "Return from a game to a lobby", null, SubcommandRunner.PLAYER_ONLY));
		
		SubcommandManager tta = new SubcommandManager("tta", "tabletop.admin", NamedTextColor.DARK_RED, mngr, this);
		tta.registerCommandList("");
		tta.register(new CmdTabletopAdminKick("kick", "Force kicks a player from a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminSetHost("sethost", "Force sets a host for a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminDisband("end", "Force sets a host for a session", null, SubcommandRunner.BOTH));
		tta.register(new CmdTabletopAdminDebug("debug", "Show debug for an instance", null, SubcommandRunner.PLAYER_ONLY));

		SubcommandManager thecrew = new SubcommandManager("thecrew", "tabletop.use", NamedTextColor.RED, mngr, this);
		thecrew.register(new CmdTheCrewPlay("play", "Plays a card in your hand", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewAcceptTasks("accepttasks", "Accepts rolled tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewAcceptTask("accepttask", "Accepts a task", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewPassTask("passtask", "Passes on accepting a task", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewRerollTasks("rerolltasks", "Rerolls tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewViewTasks("viewtasks", "Views assigned tasks", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewViewHand("viewhand", "Views a player's hand", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewMod("mod", "Views moderation list for The Crew", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewRestartRound("restartround", "Restarts the round", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewRestartGame("restartgame", "Restarts the game", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewUseSonars("usesonars", "Uses a sonars", null, SubcommandRunner.PLAYER_ONLY));
		thecrew.register(new CmdTheCrewSonar("sonar", "Brings up sonar menu", null, SubcommandRunner.PLAYER_ONLY));
	}
	
	public static NeoTabletop inst() {
		return inst;
	}
	
	public static Scheduler scheduler() {
		return BungeeCore.proxy().getScheduler();
	}
	
	public static ProxyServer proxy() {
		return BungeeCore.proxy();
	}
}
