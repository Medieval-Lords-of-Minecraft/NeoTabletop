package me.neoblade298.neotabletop;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class Game {
	protected String key, name, desc;
	protected int minPlayers = 1, maxPlayers = 4;
	protected BaseComponent[][] manual;
	public Game(File baseDir) {
		BungeeCore.loadFiles(new File(baseDir, "config.yml"), (cfg, file) -> {
			key = cfg.getString("key");
			name = cfg.getString("name");
			desc = cfg.getString("description");
			minPlayers = cfg.getInt("min-players");
			maxPlayers = cfg.getInt("max-players");
		});
		BungeeCore.loadFiles(new File(baseDir, "manual.yml"), (cfg, file) -> {
			manual = MiniMessageManager.
		});
		
		GameManager.registerGame(this);
		NeoTabletop.inst().getProxy().getLogger().log(Level.INFO, "[NeoTabletop] Registering " + key + "...");
	}
	public String getKey() {
		return key;
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
	public void displayInfo(CommandSource p) {
		Util.msgRaw(p, "&7<< &6" + name + "&7>>");
		Util.msgRaw(p, desc);
		Util.msgRaw(p, "&7=====");
		p.sendMessage(SharedUtil.createText("&8[&7Click to read the manual for the game!&8]", "Click here to read the manual!", "/tt manual " + key).create());
	}
	public void displayManual(ProxiedPlayer p) {
		displayManual(p, 1);
	}
	public void displayManual(ProxiedPlayer p, int page) {
		page--;
		if (page < 0 || page + 1 > manual.length) {
			Util.msgRaw(p, "&cThat page in the manual doesn't exist!");
			return;
		}
		p.sendMessage(manual[page]);
	}
	public abstract GameLobby<? extends GamePlayer> createLobby(String name, UUID uuid, boolean isPublic);
}
