package me.neoblade298.neotabletop.thecrew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.shared.io.Section;
import me.neoblade298.neotabletop.Game;
import me.neoblade298.neotabletop.GameLobby;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.NeoTabletop;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import me.neoblade298.neotabletop.thecrew.tasks.*;

public class TheCrew extends Game {
	private static TheCrew inst;
	private static File BASE_DIR = new File(NeoTabletop.folder(), "/The Crew");
	private static ArrayList<TheCrewCard> deck = new ArrayList<TheCrewCard>(44);
	public static LinkedList<TheCrewTask> tasks = new LinkedList<TheCrewTask>();
	
	public TheCrew() {
		super(BASE_DIR);
		inst = this;
		
		// Set up deck of cards
		for (int i = 1; i <= 9; i++) {
			deck.add(new TheCrewCard(CardType.RED, i));
			deck.add(new TheCrewCard(CardType.BLUE, i));
			deck.add(new TheCrewCard(CardType.GREEN, i));
			deck.add(new TheCrewCard(CardType.YELLOW, i));
			if (i <= 4) deck.add(new TheCrewCard(CardType.SUB, i));
		}
		
		// Set up tasks
		BungeeCore.loadFiles(new File(BASE_DIR, "tasks.yml"), (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				try {
					Section sec = cfg.getSection(key);
					TheCrewTask task = null;
					switch (sec.getString("type")) {
					case "WIN_CARDS":
						task = new WinCardsTask(sec);
						break;
					case "WIN_CARDS_TYPE_EXCLUSIVE":
						task = new WinCardsTypeExclusiveTask(sec);
						break;
					case "COMPARE_COLORS":
						task = new CompareColorsTask(sec);
						break;
					case "WIN_ONE_COLOR":
						task = new WinOneColorTask(sec);
						break;
					case "WIN_EACH_COLOR":
						task = new WinEachColorTask(sec);
						break;
					case "WIN_CARDS_COMPARE":
						task = new WinCardsCompareTask(sec);
						break;
					case "WIN_CARDS_COMPARE_MULTIPLE":
						task = new WinCardsCompareMultipleTask(sec);
						break;
					case "WIN_TRICK_USING":
						task = new WinTrickUsingTask(sec);
						break;
					case "WIN_CARDS_FINAL":
						task = new WinCardsFinalTask(sec);
						break;
					case "WIN_TRICK_TOTAL_COMPARE":
						task = new WinTrickTotalCompareTask(sec);
						break;
					case "WIN_TRICK_TOTAL_EQUAL":
						task = new WinTrickTotalEqualTask(sec);
						break;
					case "WIN_TRICK_EACH_COMPARE":
						task = new WinTrickEachCompareTask(sec);
						break;
					case "WIN_TRICK_COMPARE_COLORS":
						task = new WinTrickCompareColorsTask(sec);
						break;
					case "NO_OPEN_TRICK":
						task = new NoOpenTrickTask(sec);
						break;
					case "WIN_TRICK_EVEN_ODD":
						task = new WinTrickEvenOddTask(sec);
						break;
					case "WIN_TRICKS":
						task = new WinTricksTask(sec);
						break;
					case "COMPARE_WINS":
						task = new CompareWinsTask(sec);
						break;
					case "WIN_EDGE_TRICK":
						task = new WinEdgeTrickTask(sec);
						break;
					case "WIN_FIRST_TRICKS":
						task = new WinFirstTricksTask(sec);
						break;
					case "WIN_TRICKS_ROW":
						task = new WinTricksRowTask(sec);
						break;
					case "WIN_TRICKS_PREDICT":
						task = new WinTricksPredictTask(sec);
						break;
					}
					if (task == null) {
						NeoTabletop.logger().log(Level.WARNING, "[NeoTabletop] Failed to load task " + key);
					}
					else {
						tasks.add(task);
					}
				}
				catch (Exception e) {
					NeoTabletop.logger().log(Level.WARNING, "[NeoTabletop] Failed to load task " + key);
					e.printStackTrace();
				}
			}
		});
		Collections.shuffle(tasks);
	}
	
	public static Game inst() {
		return inst;
	}

	@Override
	public GameLobby<? extends GamePlayer> createLobby(String name, UUID uuid, boolean isPublic) {
		return new TheCrewLobby(name, uuid, isPublic);
	}
	
	public static ArrayList<TheCrewCardInstance> createDeck() {
		ArrayList<TheCrewCardInstance> deck = new ArrayList<TheCrewCardInstance>();
		for (TheCrewCard card : TheCrew.deck) {
			deck.add(card.createInstance());
		}
		Collections.shuffle(deck);
		return deck;
	}
	
	public static TheCrewTask getTask() {
		TheCrewTask task = tasks.removeFirst();
		tasks.addLast(task);
		return task;
	}
}
