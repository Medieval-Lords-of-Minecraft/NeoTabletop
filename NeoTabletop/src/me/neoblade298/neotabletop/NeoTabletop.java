package me.neoblade298.neotabletop;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class NeoTabletop extends Plugin {
	private static NeoTabletop inst;
	
	public void onEnable() {
		inst = this;
		initCommands();
    	
    	// getProxy().getPluginManager().registerListener(this, new PaystubIO());
		
		
	}
	
	private void initCommands() {
		// SubcommandManager mngr = new SubcommandManager("paystub", "paystub.use", ChatColor.RED, this);
		// mngr.registerCommandList("help");
		// mngr.register(new CmdPaystub("", "Checks your account", null, SubcommandRunner.BOTH));
		// mngr.register(new CmdPaystubApprove("approve", "Approves a pay request", "paystub.admin", SubcommandRunner.BOTH));
		// mngr.register(new CmdPaystubDeny("deny", "Denies a pay request", "paystub.admin", SubcommandRunner.BOTH));
	}
	
	public static NeoTabletop inst() {
		return inst;
	}
	
	public static TaskScheduler scheduler() {
		return inst.getProxy().getScheduler();
	}
	
	public static ProxyServer proxy() {
		return inst.getProxy();
	}
}
