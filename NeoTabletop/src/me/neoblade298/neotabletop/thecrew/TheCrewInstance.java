package me.neoblade298.neotabletop.thecrew;

import java.util.HashMap;
import java.util.UUID;

import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GamePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TheCrewInstance extends GameInstance {

	public TheCrewInstance(String name, Game game, UUID host, GameLobby lobby) {
		super(name, game, host, lobby);
		for (UUID uuid : lobby.getPlayers()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			this.players.put(p.getName().toLowerCase(), new TheCrewPlayer(uuid, name, p));
		}
	}

	@Override
	public void handleLeave(GamePlayer gp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpectate(ProxiedPlayer p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameLobby onEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void displayInfo(ProxiedPlayer viewer, ProxiedPlayer viewed) {
		// TODO Auto-generated method stub
		
	}

}
