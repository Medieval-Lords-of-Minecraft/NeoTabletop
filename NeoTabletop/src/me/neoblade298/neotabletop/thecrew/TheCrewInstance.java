package me.neoblade298.neotabletop.thecrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;

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
	// Etc
	private TaskScheduler sch = ProxyServer.getInstance().getScheduler();
	
	// Metadata
	private int difficulty;
	private GamePhase phase = GamePhase.SETUP;
	private TheCrewPlayer captain;
	private ArrayList<TheCrewPlayer> turnOrder = new ArrayList<TheCrewPlayer>();
	private int turn = 0, round = 1, totalRounds;
	
	// Task Selection
	private ArrayList<TheCrewTask> tasks;
	
	// Play phase
	private ArrayList<TheCrewCard> pile = new ArrayList<TheCrewCard>();

	public TheCrewInstance(GameLobby<TheCrewPlayer> lobby) {
		super(lobby);
		for (UUID uuid : lobby.getPlayers()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			this.players.put(p.getName().toLowerCase(), new TheCrewPlayer(uuid, name, p));
		}
		
		difficulty = (int) params.get("difficulty").get();
		
		setupGame();
	}

	@Override
	public void handleLeave(GamePlayer gp) {
		broadcast("&7Due to &e" + gp.getName() + " &7leaving, the game has ended.");
		endGame();
	}

	@Override
	public void onSpectate(ProxiedPlayer p) {
		displaySpectatorInfo(p);
	}

	@Override
	public GameLobby<TheCrewPlayer> onEnd() {
		return new TheCrewLobby(this);
	}

	@Override
	public void displayInfo(ProxiedPlayer viewer, ProxiedPlayer viewed) {
		if (players.containsKey(viewer.getName().toLowerCase())) {
			displaySpectatorInfo(viewer);
			return;
		}
		TheCrewPlayer p = players.get(viewer.getName().toLowerCase());
		boolean isHost = p.getUniqueId().equals(host);
		boolean viewerTurn = turnOrder.get(turn).getUniqueId().equals(viewer.getUniqueId());
		
		if (isHost) {
			p.getPlayer().sendMessage(SharedUtil.createText("&8[&7Click for game moderation tools&7]&8", "Click here!", "tt kicklist").create());
		}
		switch (phase) {
		case SETUP: Util.msg(viewer, "&7Game is being setup...", false);
		break;
		case WAITING: Util.msg(viewer, "&7Game is calculating something...", false);
		break;
		case ROLL_TASKS: 
			if (viewer.getUniqueId().equals(captain.getUniqueId())) {
				promptPlayer();
			}
			else {
				Util.msg(viewer, "&7Captain is deciding whether to reroll the following tasks:");
				for (TheCrewTask task : tasks) {
					Util.msg(viewer, "&7- &f" + task.getDisplay() + " &7(Difficulty: &e" + task.getDifficulty(players.size()), false);
				}
			}
			break;
		case SELECT_TASKS:
			if (viewerTurn) {
				promptPlayer();
			}
			else {
				TheCrewPlayer tcp = turnOrder.get(turn);
				viewer.sendMessage(SharedUtil.createText("&8[&7Click or hover to view accepted tasks&8]",
						createTaskHover(), "thecrew viewtasks").create());
				Util.msg(viewer, "&e" + tcp.getName() + "&7's turn to select a task:");
				for (TheCrewTask task : tasks) {
					Util.msg(viewer, "&7- &f" + task.getDisplay() + " &7(Difficulty: &e" + task.getDifficulty(players.size()), false);
				}
			}
			break;
		case PLAY:
			Util.msg(viewer, "Rounds remaining: &e" + (totalRounds - round), false);
			Util.msg(viewer, "&7Turn Order:", false);
			int num = 0;
			for (TheCrewPlayer tcp : turnOrder) {
				String text = "&7- &c" + tcp.getName() + " &6" + tcp.getWins() + "W&7";
				if (tcp.getUniqueId().equals(captain.getUniqueId())) {
					text += " (&4Captain&7)";
				}
				text += ": ";
				text += pile.size() > num ? pile.get(num).getDisplay() : "???";
				Util.msg(viewer, text);
			}
			viewer.sendMessage(SharedUtil.createText("&8[&7Click or hover to view accepted tasks&8]",
					createTaskHover(), "thecrew viewtasks").create());
			p.displayHand(viewer);
			break;
		}
			
	}
	
	public void displaySpectatorInfo(ProxiedPlayer viewer) {
		if (phase == GamePhase.PLAY) {
			Util.msg(viewer, "Rounds remaining: &e" + (totalRounds - round), false);
			Util.msg(viewer, "&7Turn Order (Hover or click to view hand):", false);
			int num = 0;
			for (TheCrewPlayer tcp : turnOrder) {
				String text = "&7- &c" + tcp.getName() + " &6" + tcp.getWins() + "W&7";
				if (tcp.getUniqueId().equals(captain.getUniqueId())) {
					text += " (&4Captain&7)";
				}
				text += ": ";
				text += pile.size() > num ? pile.get(num).getDisplay() : "???";
				viewer.sendMessage(SharedUtil.createText(text, tcp.createHandHoverText(), "thecrew viewhand " + tcp.getName()).create());
			}
			viewer.sendMessage(SharedUtil.createText("&8[&7Click or hover to view accepted tasks&8]",
					createTaskHover(), "thecrew viewtasks").create());
		}
		else {
			displayInfo(viewer, viewer);
		}
	}
	
	public void broadcastInfo() {
		for (GamePlayer gp : players.values()) {
			displayInfo(gp.getPlayer(), gp.getPlayer());
		}
		for (ProxiedPlayer spec : spectators) {
			displaySpectatorInfo(spec);
		}
	}
	
	private void setupGame() {
		// Pass out deck
		ArrayList<TheCrewCard> deck = TheCrew.createDeck();
		while (deck.size() >= players.size()) {
			totalRounds++;
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
		setFirst(captain.getUniqueId());
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
		phase = GamePhase.WAITING;
	}
	
	public void setFirst(UUID uuid) {
		while (!uuid.equals(turnOrder.get(0).getUniqueId())) {
			turnOrder.add(turnOrder.remove(turnOrder.size() - 1)); // Shift right
		}
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
			for (TheCrewTask task : tasks) {
				Util.msg(captain.getPlayer(), "&7- &f" + task.getDisplay() + " &7(Difficulty: &e" + task.getDifficulty(players.size()), false);
			}
			ComponentBuilder b = SharedUtil.createText("&8[&aAccept Tasks&8] ", "Click to accept!", "thecrew accepttasks");
			SharedUtil.appendText(b, "&8[&cReroll Tasks&8]", "Click to reroll!", "thecrew rerolltasks");
			captain.getPlayer().sendMessage(b.create());
		}
		if (phase == GamePhase.SELECT_TASKS) {
			ProxiedPlayer p = turnOrder.get(turn).getPlayer();
			p.sendMessage(SharedUtil.createText("&8[&7Click or hover to view accepted tasks&8]",
					createTaskHover(), "thecrew viewtasks").create());
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
	}
	
	public void play(ProxiedPlayer p, int num) {
		if (phase != GamePhase.PLAY) {
			Util.msg(p, "&cYou can't do that during this phase!");
			return;
		}
		
		if (!turnOrder.get(turn).getPlayer().getUniqueId().equals(p.getUniqueId())) {
			Util.msg(p, "&cIt's not your turn right now!");
			return;
		}
		
		TheCrewPlayer tcp = turnOrder.get(turn);
		TheCrewCard toPlay = tcp.getCard(num);
		if (!pile.isEmpty()) { // First card can choose any card without restriction
			TheCrewCard topCard = pile.get(0);
			if (!toPlay.isSimilar(topCard) && tcp.hasSimilarCard(pile.get(0))) {
				Util.msg(p, "&cYou must play a card that's the same type as the top card!");
				return;
			}
		}
		
		pile.add(pile.size(), tcp.removeCard(num));
		broadcast("&e" + p.getName() + " &7plays " + toPlay.getDisplay());

		int time = 1;
		sch.schedule(NeoTabletop.inst(), () -> {
			broadcastInfo();
		}, time++, TimeUnit.SECONDS);

		if (pile.size() == players.size()) {
			sch.schedule(NeoTabletop.inst(), () -> {
				calculateTrickWinner();
			}, time++, TimeUnit.SECONDS);
		}
		
		sch.schedule(NeoTabletop.inst(), () -> {
			advanceTurn();
		}, time, TimeUnit.SECONDS);
	}
	
	public void calculateTrickWinner() {
		TheCrewCard winningCard = pile.get(0);
		int winningPlayer = 0;
		for (int i = 1; i < pile.size(); i++) {
			TheCrewCard comp = pile.get(i);
			// Condition 1: Cards are the same type and comp is higher value
			// Condition 2: Comp is sub and winning card is not
			if (comp.getType() == winningCard.getType() && comp.getValue() > winningCard.getValue()
					|| comp.getType() == CardType.SUB && winningCard.getType() != CardType.SUB) {
				winningCard = comp;
				winningPlayer = i;
			}
		}
		
		TheCrewPlayer tcp = turnOrder.get(winningPlayer);
		tcp.winTrick(pile);
		broadcast("&e" + tcp.getName() + " &7won the round with " + winningCard.getDisplay() + "&7!");
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
		}, time++, TimeUnit.SECONDS);
		phase = temp;

		sch.schedule(NeoTabletop.inst(), () -> {
			phase = temp;
			promptPlayer();
		}, time, TimeUnit.SECONDS);
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
		}, time++, TimeUnit.SECONDS);

		sch.schedule(NeoTabletop.inst(), () -> {
			phase = temp;
			if (phase == GamePhase.PLAY) {
				broadcastInfo();
			}
			promptPlayer();
		}, time, TimeUnit.SECONDS);
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
		phase = GamePhase.WAITING;
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
			phase = GamePhase.ROLL_TASKS;
		}, time++, TimeUnit.SECONDS);
	}
	
	private String createTaskHover() {
		String text = "";
		int count = 0;
		for (TheCrewPlayer tcp : turnOrder) {
			text += "&e" + tcp.getName() + "&7:";
			for (TheCrewTask task : tcp.getTasks()) {
				if (task.isComplete()) {
					text += "\n&7&m&o- " + ChatColor.stripColor(task.getDisplay());
				}
				else {
					text += "\n&7- " + task.getDisplay();
				}
			}
			if (++count < turnOrder.size()) {
				text += "\n";
			}
		}
		return text;
	}
	
	public void displayTasks(ProxiedPlayer p) {
		for (TheCrewPlayer tcp : turnOrder) {
			Util.msg(p, "&e" + tcp.getName() + "&7:", false);
			for (TheCrewTask task : tcp.getTasks()) {
				if (task.isComplete()) {
					Util.msg(p, "&7&m&o- " + ChatColor.stripColor(task.getDisplay()), false);
				}
				else {
					Util.msg(p, "&7- " + task.getDisplay(), false);
				}
			}
		}
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
	
	public void viewHand(String name, ProxiedPlayer viewer) {
		TheCrewPlayer tcp = players.get(name.toLowerCase());
		tcp.displayHand(viewer);
	}

	private enum GamePhase {
		SETUP,
		WAITING,
		ROLL_TASKS,
		SELECT_TASKS,
		PLAY;
	}
}
