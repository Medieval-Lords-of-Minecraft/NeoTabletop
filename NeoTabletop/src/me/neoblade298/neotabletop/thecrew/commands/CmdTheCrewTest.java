package me.neoblade298.neotabletop.thecrew.commands;

import java.util.ArrayList;
import java.util.Random;
import me.neoblade298.neocore.bungee.commands.Subcommand;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neotabletop.thecrew.TheCrew;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import net.md_5.bungee.api.CommandSender;

public class CmdTheCrewTest extends Subcommand {
	private static int iterations = 0;
	private static Random gen;

	// /thecrew test
	public CmdTheCrewTest(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		hidden = true;
		
		gen = new Random();
	}

	@Override
	public void run(CommandSender s, String[] args) {
		int numPlayers = 5;
		
		// Create 5 players with cards
		ArrayList<TheCrewCardInstance> cards = TheCrew.createDeck();
		ArrayList<TheCrewPlayer> players = new ArrayList<TheCrewPlayer>();
		for (int i = 0; i < numPlayers; i++) {
			TheCrewPlayer p = new TheCrewPlayer(null, null);
			players.add(p);
			for (int j = 0; j < 40 / numPlayers; j++) {
				p.addCard(cards.remove(0));
			}
			p.sortHand();
		}

		for (int i = 0; i < 40 / 5; i++) {
			Util.msgRaw(s, "Testing with " + (8 - i) + " cards");
			
			long start = System.currentTimeMillis();
			iterations = 0;
			checkTaskBruteForce(s, players);
			long stop = System.currentTimeMillis();
			Util.msgRaw(s, "Took " + (stop - start) + "ms, " + iterations + " iterations");
			
			for (TheCrewPlayer p : players) {
				p.playCard(gen.nextInt(p.getHand().size()));
			}
		}
	}

	private boolean checkTaskBruteForce(CommandSender s, ArrayList<TheCrewPlayer> players) {
		for (TheCrewCard topCard : players.get(0).getHand()) {
			iterations++;
			if (topCard.getType() == CardType.SUB) continue;
			
			
			if (checkTaskBruteForceRecurse(s, topCard, players, 1, topCard.getValue())) {
				Util.msgRaw(s, "Player 0 plays " + topCard.getType() + " " + topCard.getValue());
				return true;
			}
		}
		return false;
	}
	
	private boolean checkTaskBruteForceRecurse(CommandSender s, TheCrewCard topCard, ArrayList<TheCrewPlayer> players, int turn, int sum) {
		for (TheCrewCard card : players.get(turn).getHand()) {
			iterations++;
			if (card.getType() == CardType.SUB) continue;
			if (!isPlayable(topCard, card, players.get(turn))) continue;

			// Recurse case
			if (turn + 1 < players.size()) {
				if (checkTaskBruteForceRecurse(s, topCard, players, turn + 1, sum + card.getValue())) {
					Util.msgRaw(s, "Player " + turn + " plays " + card.getType() + " " + card.getValue());
					return true;
				}
			}
			
			// Base case
			else {
				int temp = sum + card.getValue();
				if (temp == 22 || temp == 23) {
					Util.msgRaw(s, "Possible. Player " + turn + " plays " + card.getType() + " " + card.getValue());
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isPlayable(TheCrewCard topCard, TheCrewCard toPlay, TheCrewPlayer p) {
		if (topCard.getType() == toPlay.getType()) { // If card is same type,  can play
			return true;
		}
		
		
		// If card is different type, check if there is a same type
		for (TheCrewCard card : p.getHand()) {
			iterations++;
			if (card.getType() == topCard.getType()) {
				return false;
			}
		}
		return true;
	}
}
