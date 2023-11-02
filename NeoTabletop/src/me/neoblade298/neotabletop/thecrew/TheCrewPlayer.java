package me.neoblade298.neotabletop.thecrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.UUID;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.SonarType;
import me.neoblade298.neotabletop.thecrew.tasks.CardMatcher;
import me.neoblade298.neotabletop.thecrew.tasks.TheCrewTask;
import net.md_5.bungee.api.chat.ComponentBuilder;
import com.velocitypowered.api.proxy.Player;

public class TheCrewPlayer extends GamePlayer {
	private LinkedList<TheCrewCardInstance> hand = new LinkedList<TheCrewCardInstance>();
	private LinkedList<TheCrewCardInstance> revealedCards = new LinkedList<TheCrewCardInstance>();
	private LinkedList<TheCrewTask> tasks = new LinkedList<TheCrewTask>();
	private ArrayList<TheCrewCardInstance> cardsWon = new ArrayList<TheCrewCardInstance>();
	private TreeSet<Integer> cardValues = new TreeSet<Integer>();
	private TheCrewCardInstance lastPlayed;
	private TheCrewInstance inst;
	private int tricksWon = 0, sonarTokens = 0;

	public TheCrewPlayer(UUID uuid, Player p, TheCrewInstance inst) {
		super(uuid, p);
		this.inst = inst;
	}
	
	public TheCrewInstance getInstance() {
		return inst;
	}
	
	public TheCrewCardInstance getCard(int num) {
		return hand.get(num);
	}
	
	public TheCrewCardInstance playCard(int num) {
		lastPlayed = hand.remove(num);
		
		// If we have another card with same value, don't remove from cardValues
		boolean removeValue = true;
		for (TheCrewCard card : hand) {
			if (card.getValue() == lastPlayed.getValue() && card.getType() != CardType.SUB) {
				removeValue = false;
				break;
			}
		}
		if (removeValue) cardValues.remove(lastPlayed.getValue());
		
		if (lastPlayed.getType() != CardType.SUB) cardValues.add(lastPlayed.getValue());
		return lastPlayed;
	}
	
	public void addCard(TheCrewCardInstance card) {
		hand.add(card);
		if (card.getType() != CardType.SUB) cardValues.add(card.getValue());
	}
	
	public void addTask(TheCrewTask task) {
		tasks.add(task);
	}
	
	public void setSonarTokens(int tokens) {
		this.sonarTokens = tokens;
	}
	
	public int getSonarTokens() {
		return this.sonarTokens;
	}
	
	public void sortHand() {
		Collections.sort(hand, new Comparator<TheCrewCard>() {
			@Override
			public int compare(TheCrewCard c1, TheCrewCard c2) {
				int c1p = c1.getType().getSortPriority();
				int c2p = c2.getType().getSortPriority();
				int comp = Integer.compare(c1p, c2p);
				if (comp != 0) return comp;
				
				return Integer.compare(c1.getValue(), c2.getValue());
			}
		});
	}
	
	public void displayHand(Player viewer) {
		boolean isOwner = viewer.getUniqueId().equals(uuid);
		
		ComponentBuilder b = SharedUtil.createText((isOwner ? "Hand" : p.getUsername() + "'s hand") + ": ");
		if (hand.isEmpty()) {
			viewer.sendMessage(SharedUtil.appendText(b, "empty!").create());
			return;
		}
		
		int pos = 0;
		Iterator<TheCrewCardInstance> iter = hand.iterator();
		SharedUtil.appendText(b, iter.next().getDisplay(), isOwner ? "Click to play!" : null, "/thecrew play " + pos);
		while (iter.hasNext()) {
			SharedUtil.appendText(b, " ");
			SharedUtil.appendText(b, iter.next().getDisplay(), isOwner ? "Click to play!" : null, "/thecrew play " + ++pos);
		}
		viewer.sendMessage(b.create());
		if (isOwner) displaySonarButton(viewer);
	}
	
	public void displaySonarButton(Player viewer) {
		if (sonarTokens == 0) return;
		ComponentBuilder b = SharedUtil.createText("&8[&7Click to use a sonar token&8]",
				"You have &e" + sonarTokens + " &7tokens remaining!", sonarTokens > 0 ? "/thecrew usesonars" : null);
		viewer.sendMessage(b.create());
	}
	
	public void displaySonarOptions() {
		TheCrewCard curr, prev, next;
		ComponentBuilder b = new ComponentBuilder("§7Use sonar token on: ");
		if (hand.size() == 0) return;
		else if (hand.size() == 1) {
			p.sendMessage(buildSonarOption(b, null, hand.getFirst(), null).create());
			return;
		}
		else {
			Iterator<TheCrewCardInstance> iter = hand.iterator();
			curr = iter.next();
			next = iter.next();
			while (iter.hasNext()) {
				prev = curr;
				curr = next;
				next = iter.next();
				
				buildSonarOption(b, prev, curr, next);
			}
			
			prev = curr;
			curr = next;
			next = null;
			buildSonarOption(b, prev, curr, next);
			p.sendMessage(b.create());
		}
		
	}
	
	private ComponentBuilder buildSonarOption(ComponentBuilder b, TheCrewCard prev, TheCrewCard curr, TheCrewCard next) {
		boolean prevDif = prev == null || prev.getType() != curr.getType();
		boolean nextDif = next == null || next.getType() != curr.getType();
		
		if (prevDif && nextDif) {
			SharedUtil.appendText(b, curr.getDisplay() + " ",
					"Click to use sonar token", "/thecrew sonar " + curr.toCardMatcher() + " ONLY");
		}
		else if (prevDif) {
			SharedUtil.appendText(b, curr.getDisplay() + " ",
					"Click to use sonar token", "/thecrew sonar " + curr.toCardMatcher() + " MIN");
		}
		else if (nextDif) {
			SharedUtil.appendText(b, curr.getDisplay() + " ",
					"Click to use sonar token", "/thecrew sonar " + curr.toCardMatcher() + " MAX");
		}
		else if (nextDif) {
			SharedUtil.appendText(b, curr.getDisplay() + " ",
					"&cYou may only use sonar tokens on\n" +
							"&ccards that are the max, min, or only\n" +
							"&ccard of that color.", null);
		}
		return b;
	}
	
	public boolean useSonarToken(CardMatcher cm, SonarType stype) {
		if (sonarTokens == 0) {
			Util.msg(p, "&cYou don't have any sonar tokens!");
			return false;
		}
		
		for (TheCrewCardInstance card : revealedCards) {
			if (cm.match(card)) {
				Util.msg(p, "&cYou've already revealed this card!");
				return false;
			}
		}
		
		TheCrewCardInstance card = null;
		for (TheCrewCardInstance c : hand) {
			if (cm.match(c)) {
				card = c;
			}
		}
		
		if (card == null) {
			Util.msg(p, "&cYou don't have this card!");
			return false;
		}
		
		CardType type = card.getType();
		inst.broadcast("&e" + getName() + "&f reveals that " + card.getDisplay() + " &fis their &e" +
				stype.getDisplay() + type.getColor() + " " + type.getDisplay() + " &fcard.");
		sonarTokens--;
		revealedCards.add(card);
		return true;
	}
	
	// Only for spectators
	public String createHandHoverText() {
		String text = p.getUsername() + "'s hand:";
		if (hand.isEmpty()) text += " empty!";
		else {
			for (TheCrewCard card : hand) {
				text += " " + card.getDisplay();
			}
		}
		return text;
	}
	
	public void winTrick(ArrayList<TheCrewCardInstance> pile) {
		tricksWon++;
		cardsWon.addAll(pile);
		pile.clear();
	}
	
	public boolean hasSimilarCard(TheCrewCard comp) {
		for (TheCrewCard card : hand) {
			if (card.getType() == comp.getType()) {
				return true;
			}
		}
		return false;
	}
	
	public LinkedList<TheCrewTask> getTasks() {
		return tasks;
	}
	
	public int getWins() {
		return tricksWon;
	}
	
	public ArrayList<TheCrewCardInstance> getCardsWon() {
		return cardsWon;
	}
	
	public LinkedList<TheCrewCardInstance> getHand() {
		return hand;
	}
	
	public TheCrewCardInstance getLastPlayed() {
		return lastPlayed;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TheCrewPlayer) {
			TheCrewPlayer tcp = (TheCrewPlayer) o;
			return tcp.getUniqueId().equals(uuid);
		}
		return false;
	}
	
	public int getMinCard(boolean includeSubs) {
		return cardValues.first();
	}
	
	public int getMaxCard(boolean includeSubs) {
		return cardValues.last();
	}
	
	// List of available values not including subs
	public TreeSet<Integer> getCardValues() {
		return cardValues;
	}
	
	public void addWin(int num) {
		tricksWon += num;
	}
	
	public LinkedList<TheCrewCardInstance> getRevealedCards() {
		return revealedCards;
	}
}
