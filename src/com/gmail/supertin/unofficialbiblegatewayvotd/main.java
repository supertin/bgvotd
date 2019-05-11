package com.gmail.supertin.unofficialbiblegatewayvotd;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;
import org.bukkit.scheduler.BukkitScheduler;

public class main extends JavaPlugin {
	//Various global values. Pretty sure this is not the right way to do this, but it works.
	public static String verse;
	public static String reference;
	public static String bibleversion; //This one is particularly bad... It's only checked in votdUpdate.
	public static int refreshtime; 
	public static int broadcasttime;

	@Override
	public void onEnable() {
		// Make sure we have a config file, then get the values we need.
		this.saveDefaultConfig();
		bibleversion = this.getConfig().getString("bibleversion");
		refreshtime = this.getConfig().getInt("refresh");
		broadcasttime = this.getConfig().getInt("broadcast");

		// Scheduler for refreshing the verse.
        BukkitScheduler refreshScheduler = getServer().getScheduler();
        refreshScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	@Override
            public void run() {
        		votdUpdate.main(); }
        	}, 0L, 20*60*60*refreshtime); //20 ticks * 60 seconds * 60 minutes * refreshtime (default 24 hours)
        
        // Scheduler for broadcasting the verse via chat.
        if(broadcasttime > 0) {
        	Bukkit.getLogger().info("Broadcasting verse every " + broadcasttime + " minutes.");
	        BukkitScheduler broadcastScheduler = getServer().getScheduler();
	        broadcastScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	        	@Override
	        	public void run() {
	        		// Broadcast the verse to all players.
	    			Bukkit.broadcastMessage(main.verse);
	    			Bukkit.broadcastMessage(main.reference);
	
	        	}
	        }, 0L, 20*60*broadcasttime);
		}
	}

	@Override
	public void onDisable() {
		// This plugin currently doesn't handle being disabled.
	}

	// Handler for commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Check which command got sent.
		switch(cmd.getName()) {
		case "votd":
			//Check if the player has permission to get the current VOTD
			if(sender.hasPermission("votd.votd")) {
				if(verse != null) {
					//Player is allowed - send them the VOTD.
					sender.sendMessage(ChatPaginator.wordWrap(verse, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH));
					sender.sendMessage(reference);
				}
				else {
					sender.sendMessage("No VOTD available");
				}
			}
			else {
				//Player is denied VOTD access. This is NOT default!
				sender.sendMessage("Server admins have denied you access to the Bible Gateway VOTD.");
			}
			break;
		case "votdrefresh":
			//If the player has permission to refresh...
			if(sender.hasPermission("votd.votdrefresh")) {
				//Run the refresh routine
				votdUpdate.main();
			}
			else {
				//Player doesn't have permission to do this. Let them know. This is the default...
				sender.sendMessage("You don't have permission to manually update the VOTD.");
			}
			break;
		default:
			break;
		}
		return true;
	}
}

