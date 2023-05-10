package me.neoblade298.neotabletop.thecrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GameInstance;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.NeoTabletop;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import me.neoblade298.neotabletop.thecrew.tasks.TheCrewTask;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class TheCrewInstance extends GameInstance<TheCrewPlayer> {
	private GamePhase phase = GamePhase.SETUP;
	private TaskScheduler sch = ProxyServer.getInstance().getScheduler();
	
	private int difficulty;
	private TheCrewPlayer captain;
	private ArrayList<TheCrewPlayer> turnOrder = new ArrayList<TheCrewPlayer>();
	private int turn = 0, round = 1;
	private ArrayList<TheCrewTask> tasks;

	public TheCrewInstance(UUID host, GameLobby<TheCrewPlayer> lobby) {
		super(host, lobby);
		for (UUID uuid : lobby.getPlayers()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			this.players.put(p.getName().toLowerCase(), new TheCrewPlayer(uuid, name, p));
		}
		
		int difficulty = (int) params.get("difficulty").get();
		
		setupGame();
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
	public GameLobby<TheCrewPlayer> onEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void displayInfo(ProxiedPlayer viewer, ProxiedPlayer viewed) {
		// TODO Auto-generated method stub
		
	}
	
	private void setupGame() {
		// Pass out deck
		ArrayList<TheCrewCard> deck = TheCrew.createDeck();
		while (deck.size() >= players.size()) {
			for (TheCrewPlayer p : players.values()) {
				TheCrewCard card = deck.remove(0);
				p.addCard(card);
				if (card.getType() == CardType.SUB && card.getValue() == 4) {
					captain = p;
				}
			}
		}
		
		// Announce captain and turn order
		turnOrder.addAll(players.values());
		Collections.sort(turnOrder, new Comparator<TheCrewPlayer>() {
			@Override
			public int compare(TheCrewPlayer c1, TheCrewPlayer c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
		while (!captain.getUniqueId().equals(turnOrder.get(0).getUniqueId())) {
			turnOrder.add(turnOrder.remove(turnOrder.size() - 1)); // Shift right until captain is first
		}
		broadcast("The captain is &e" + captain.getName() + "&7!");
		broadcast("Turn order:");
		for (TheCrewPlayer p : turnOrder) {
			p.sortHand();
			broadcast("- &c" + p.getName());
		}
		
		// Sort and display hands
		for (TheCrewPlayer p : turnOrder) {
			p.displayHand(p.getPlayer());
			for (ProxiedPlayer spec : spectators) {
				p.displayHand(spec);
			}
		}

		rollTasks(2);
		phase = GamePhase.ROLL_TASKS;
	}
	
	public void displayHand(String viewed, ProxiedPlayer viewer) {
		TheCrewPlayer p = players.get(viewed.toLowerCase());
		if (p == null) {
			Util.msg(viewer, "&cThat player isn't in this game!");
			return;
		}
	}
	
	public void promptPlayer() {
		if (phase == GamePhase.ROLL_TASKS) {
			ProxiedPlayer p = turnOrder.get(turn).getPlayer();
			Util.msg(p, "&7Choose a task:");
			int num = 0;
			for (TheCrewTask task : tasks) {
				ComponentBuilder b = SharedUtil.createText("&7- &f" + task.getDisplay() + " &7(Difficulty: &e" + task.getDifficulty(players.size()),
						"Click to accept!", "thecrew accepttask " + num++);
				p.sendMessage(b.create());
			}

			int remainingPlayers = players.size() - (turn + 1);
			if (tasks.size() >= remainingPlayers) {
				p.sendMessage(SharedUtil.createText("&8[&7Click to pass&8]", "This means players after you will\nhave to accept these tasks!",
						"thecrew passtask").create());
			}
		}
		
		else if (phase == GamePhase.PLAY) {
			
		}
	}
	
	public void acceptTask(ProxiedPlayer p, int num) {
		if (phase != GamePhase.ROLL_TASKS) {
			Util.msg(p, "&cYou can't do that during this phase!");
			return;
		}
		
		if (!turnOrder.get(turn).getPlayer().getUniqueId().equals(p.getUniqueId())) {
			Util.msg(p, "&cIt's not your turn right now!");
			return;
		}
		
		TheCrewTask task = tasks.remove(num);
		turnOrder.get(turn).addTask(task);
		broadcast("&e" + p.getName() + " &7has accepted task: &f" + task.getDisplay());
		
		if (tasks.isEmpty()) {
			broadcast("Task assignment completed! Starting game...");
			phase = GamePhase.PLAY;
			startRounds();
		}
		else {
			advanceTurn();
		}
	}
	
	public void passTask(ProxiedPlayer p) {
		if (phase != GamePhase.ROLL_TASKS) {
			Util.msg(p, "&cYou can't do that during this phase!");
			return;
		}
		
		if (!turnOrder.get(turn).getPlayer().getUniqueId().equals(p.getUniqueId())) {
			Util.msg(p, "&cIt's not your turn right now!");
			return;
		}
		
		int remainingPlayers = players.size() - (turn + 1);
		if (tasks.size() < remainingPlayers) {
			Util.msg(p, "&cAll remaining tasks must be accepted this round!");
			return;
		}
		
		broadcast("&e" + p.getName() + " &7has chosen not to accept a task.");
		advanceTurn();
	}
	
	public void acceptTasks(ProxiedPlayer p) {
		if (phase != GamePhase.ROLL_TASKS) {
			Util.msg(p, "&cYou can't do that during this phase!");
			return;
		}
		
		if (!captain.getPlayer().getUniqueId().equals(p.getUniqueId())) {
			Util.msg(p, "&cOnly the captain may reroll tasks!");
			return;
		}
		
		broadcast("The captain chose to &aaccept &7the tasks!");
		phase = GamePhase.SELECT_TASKS;
		startRounds();
	}
	
	public void startRounds() {
		GamePhase temp = phase;
		phase = GamePhase.WAITING;
		int time = 1;
		round = 1;
		turn = 0;
		sch.schedule(NeoTabletop.inst(), () -> {
			broadcast("Round 1...");
		}, time++, TimeUnit.SECONDS);
		
		sch.schedule(NeoTabletop.inst(), () -> {
			broadcast("&e" + turnOrder.get(turn).getName() + "&7's turn");
		}, time, TimeUnit.SECONDS);
		phase = temp;
		
		promptPlayer();
	}
	
	public void advanceTurn() {
		GamePhase temp = phase;
		phase = GamePhase.WAITING;
		int time = 1;
		turn++;
		if (turn >= players.size()) {
			turn = 0;
			sch.schedule(NeoTabletop.inst(), () -> {
				broadcast("Round " + (++round) + "...");
			}, time++, TimeUnit.SECONDS);
		}
		sch.schedule(NeoTabletop.inst(), () -> {
			broadcast("&e" + turnOrder.get(turn).getName() + "&7's turn");
		}, time, TimeUnit.SECONDS);
		phase = temp;

		promptPlayer();
	}
	
	public void rerollTasks(ProxiedPlayer p) {
		if (phase != GamePhase.ROLL_TASKS) {
			Util.msg(p, "&cYou can't do that during this phase!");
			return;
		}
		
		if (!captain.getPlayer().getUniqueId().equals(p.getUniqueId())) {
			Util.msg(p, "&cOnly the captain may reroll tasks!");
			return;
		}
		
		broadcast("The captain chose to &creroll &7the tasks!");
		rollTasks(1);
	}
	
	// Use time 2 for first setup, time 1 for reroll
	private void rollTasks(int time) {
		sch.schedule(NeoTabletop.inst(), () -> {
			broadcast("Rolling tasks...");
		}, time, TimeUnit.SECONDS);

		time += 2;
		tasks = getTasks();
		for (TheCrewTask task : tasks) {
			sch.schedule(NeoTabletop.inst(), () -> {
				broadcast("&7- &f" + task.getDisplay() + " &7(Difficulty: &e" + task.getDifficulty(players.size()));
			}, time++, TimeUnit.SECONDS);
		}

		sch.schedule(NeoTabletop.inst(), () -> {
			broadcast("The captain is responsible for accepting tasks or rerolling! Note that some tasks "
					+ "may be impossible together. Look carefully before accepting them!");
		}, time++, TimeUnit.SECONDS);

		sch.schedule(NeoTabletop.inst(), () -> {
			ComponentBuilder b = SharedUtil.createText("&8[&aAccept Tasks&8] ", "Click to accept!", "thecrew accepttasks");
			SharedUtil.appendText(b, "&8[&cReroll Tasks&8]", "Click to reroll!", "thecrew rerolltasks");
			captain.getPlayer().sendMessage(b.create());
		}, time++, TimeUnit.SECONDS);
	}
	
	private ArrayList<TheCrewTask> getTasks() {
		int diff = difficulty;
		ArrayList<TheCrewTask> tasks = new ArrayList<TheCrewTask>();
		while (diff > 0) {
			TheCrewTask task = TheCrew.getTask();
			if (task.getDifficulty(players.size()) > diff) continue;
			
			diff -= task.getDifficulty(players.size());
			tasks.add(task);
		}
		return tasks;
	}

	private enum GamePhase {
		SETUP,
		WAITING,
		ROLL_TASKS,
		SELECT_TASKS,
		PLAY;
	}
}
