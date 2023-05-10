package me.neoblade298.neotabletop.thecrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import me.neoblade298.neocore.shared.util.SharedUtil;
import me.neoblade298.neotabletop.GamePlayer;
import me.neoblade298.neotabletop.thecrew.tasks.TheCrewTask;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TheCrewPlayer extends GamePlayer {
	private LinkedList<TheCrewCard> hand = new LinkedList<TheCrewCard>();
	private LinkedList<TheCrewTask> tasks = new LinkedList<TheCrewTask>();
	private ArrayList<TheCrewCard> cardsWon = new ArrayList<TheCrewCard>();
	private int tricksWon = 0;

	public TheCrewPlayer(UUID uuid, String name, ProxiedPlayer p) {
		super(uuid, name, p);
	}
	
	public TheCrewCard getCard(int num) {
		return hand.get(num);
	}
	
	public TheCrewCard removeCard(int num) {
		return hand.remove(num);
	}
	
	public void addCard(TheCrewCard card) {
		hand.add(card);
	}
	
	public void addTask(TheCrewTask task) {
		tasks.add(task);
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
	
	public void displayHand(ProxiedPlayer viewer) {
		boolean isOwner = viewer.getUniqueId().equals(uuid);
		
		ComponentBuilder b = SharedUtil.createText((isOwner ? "" : p.getName() + "'s ") + "hand: ");
		if (hand.isEmpty()) {
			viewer.sendMessage(SharedUtil.appendText(b, "empty!").create());
			return;
		}
		
		int pos = 0;
		Iterator<TheCrewCard> iter = hand.iterator();
		SharedUtil.appendText(b, iter.next().getDisplay(), isOwner ? "Click to play!" : null, "thecrew play " + pos);
		while (iter.hasNext()) {
			SharedUtil.appendText(b, " ");
			SharedUtil.appendText(b, iter.next().getDisplay(), isOwner ? "Click to play!" : null, "thecrew play " + ++pos);
		}
		viewer.sendMessage(b.create());
	}
	
	// Only for spectators
	public String createHandHoverText() {
		String text = p.getName() + "'s hand:";
		if (hand.isEmpty()) text += " empty!";
		else {
			for (TheCrewCard card : hand) {
				text += " " + card.getDisplay();
			}
		}
		return text;
	}
	
	public void winTrick(ArrayList<TheCrewCard> pile) {
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
}
