package me.neoblade298.neotabletop;

import java.io.File;
import java.util.UUID;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.shared.messaging.MessagingManager;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class Game {
	protected String name, desc;
	protected BaseComponent[][] manual;
	public Game(File baseDir) {
		BungeeCore.loadFiles(new File(baseDir, "config.yml"), (cfg, file) -> {
			name = cfg.getString("name");
			desc = cfg.getString("description");
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
	public abstract GameLobby createLobby(String name, UUID uuid, boolean isPublic);
}
