package me.neoblade298.neotabletop;

import java.io.File;
import java.util.UUID;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.messaging.MessagingManager;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class Game {
	protected String name, desc;
	protected int minPlayers = 1, maxPlayers = 4;
	protected BaseComponent[][] manual;
	public Game(File baseDir) {
		BungeeCore.loadFiles(new File(baseDir, "config.yml"), (cfg, file) -> {
			name = cfg.getString("name");
			desc = cfg.getString("description");
			minPlayers = cfg.getInt("min-players");
			maxPlayers = cfg.getInt("max-players");
		});
		BungeeCore.loadFiles(new File(baseDir, "manual.yml"), (cfg, file) -> {
			manual = MessagingManager.parsePage(cfg);
		});
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return desc;
	}
	public BaseComponent[][] getManual() {
		return manual;
	}
	public int getMinPlayers() {
		return minPlayers;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void displayInfo(ProxiedPlayer p) {
		Util.msg(p, "&7<< &6" + name + "&7>>", false);
		Util.msg(p, desc, false);
		Util.msg(p, "&7=====", false);
		p.sendMessage(SharedUtil.createText("&8[&7Click to read the manual for the game!&8]", "Click here to read the manual!", "tt manual " + name).create());
	}
	public abstract GameLobby createLobby(String name, UUID uuid, boolean isPublic);
}
