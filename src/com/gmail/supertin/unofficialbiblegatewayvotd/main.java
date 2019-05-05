package com.gmail.supertin.unofficialbiblegatewayvotd;


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

	/* The timer code is currently broken enough that it causes errors
	 * 
	 * Also commented out below is the "timer.schedule" line that enables the timer
	// Set up a timer to auto-refresh the verse of the day.
	Timer timer = new Timer();
	TimerTask refreshVerse = new TimerTask() {
		@Override
		public void run () {
			// This is the code that will run on the timer
			votdUpdate.main();
		}
	};

*/

	@Override
	public void onEnable() {
		// Make sure we have a config file, then get the values we need.
		this.saveDefaultConfig();
		bibleversion = this.getConfig().getString("bibleversion");
		refreshtime = this.getConfig().getInt("refresh");

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	@Override
            public void run() {
                // Do something
        		votdUpdate.main();
            }
        }, 0L, 20*60*60*refreshtime); //20 ticks * 60 seconds * 60 minutes * refreshtime (default 24 hours)
	}

	@Override
	public void onDisable() {
		// This plugin doesn't handle being disabled.
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

