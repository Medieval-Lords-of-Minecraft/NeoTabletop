package me.neoblade298.neotabletop.thecrew.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neotabletop.thecrew.TheCrewCard;
import me.neoblade298.neotabletop.thecrew.TheCrewCardInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewInstance;
import me.neoblade298.neotabletop.thecrew.TheCrewPlayer;
import me.neoblade298.neotabletop.thecrew.TheCrewCard.CardType;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.config.Configuration;

public class WinTrickTotalCompareTask extends TheCrewTask {
	protected HashMap<Integer, Integer> totals = new HashMap<Integer, Integer>();
	protected boolean more; // Either more or less
	protected int total;
	
	@Override
	public void showDebug(CommandSource s) {
		Util.msgRaw(s, "more: " + more + ", total: " + total);
	}
	
	public WinTrickTotalCompareTask(Configuration cfg) {
		super(cfg);
		
		Configuration totalSec = cfg.getSection("total");
		for (String key : totalSec.getKeys()) {
			int ikey = Integer.parseInt(key);
			totals.put(ikey, totalSec.getInt(key));
		}

		display = "Win a trick with a total value " + (more ? "greater" : "less") + " than " + totals.get(3) +
				" (3-player), " + totals.get(4) + " (4-player), " + totals.get(5) + " (5-player). *No subs";
		more = cfg.getString("comparator").equals(">");
		
	}
	
	public WinTrickTotalCompareTask(TheCrewPlayer owner, WinTrickTotalCompareTask src, TheCrewInstance inst) {
		super(owner, src, inst);

		this.total = inst.getPlayers().size();
		display = "Win a trick with a total value " + (more ? "greater" : "less") + " than &e" + total + "&f. *No subs";
		this.total = src.total;
		this.more = src.more;
	}

	@Override
	public WinTrickTotalCompareTask clone(TheCrewPlayer owner, TheCrewInstance inst) {
		return new WinTrickTotalCompareTask(owner, this, inst);
	}
	
	@Override
	public boolean hasFailed(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		int total = 0;
		boolean containsSub = false;
		if (winner.equals(owner)) {
			for (TheCrewCard card : pile) {
				if (card.getType() == CardType.SUB) {
					containsSub = true;
					break;
				}
				
				total += card.getValue();
			}
		}
		if (more ? total > this.total : total < this.total && !containsSub) return false; // Succeeded
		
		int potential = 0;
		for (TheCrewPlayer p : inst.getPlayers().values()) {
			potential += (more ? p.getMaxCard(false) : p.getMinCard(false));
		}
		return more ? potential <= this.total : potential >= this.total;
	}

	@Override
	public boolean update(TheCrewInstance inst, TheCrewPlayer winner, ArrayList<TheCrewCardInstance> pile) {
		int total = 0;
		if (winner.equals(owner)) {
			for (TheCrewCard card : pile) {
				if (card.getType() == CardType.SUB) return false;
				
				total += card.getValue();
			}
		}
		
		return more ? total > this.total : total < this.total;
	}

	@Override
	public void reset() {}
}
