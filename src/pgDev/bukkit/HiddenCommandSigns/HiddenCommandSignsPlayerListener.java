package pgDev.bukkit.HiddenCommandSigns;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

/**
 * Handle events for all Player related events
 * @author pgDev
 */
public class HiddenCommandSignsPlayerListener extends PlayerListener {
    private final HiddenCommandSigns plugin;

    public HiddenCommandSignsPlayerListener(HiddenCommandSigns instance) {
        plugin = instance;
    }
    
    // Respond to right-click
    @SuppressWarnings("static-access")
	public void onPlayerInteract(PlayerInteractEvent event) {
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.hasPermissions(event.getPlayer(), "scs.use")) {
    		if (plugin.debug) {
        		System.out.println(event.getPlayer().getName() + " right-clicked a block.");
        	}
    		
    		if (event.getClickedBlock() != null && plugin.blockListener.isSign(event.getClickedBlock())) {
    			Sign theSign = (Sign)event.getClickedBlock().getState();
    			
    			if (plugin.debug) {
            		System.out.println("And it was a sign with first line text: " + theSign.getLine(0));
            	}
    			
	    		if (plugin.blockListener.isHCS(theSign)) {
	    			if (plugin.debug) {
	            		System.out.println(event.getPlayer().getName() + " right-clicked a hiddencommandsign.");
	            	}
	    			
	    			// Kill block placing
	    			event.setCancelled(true);
	    			
	    			String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
	    			try {
		    			HiddenCommand trueCommand = plugin.commandLinks.get(signText);
		    			LinkedList<String> tempPerms = new LinkedList<String>();
		    			if (trueCommand.permissions != null) {
			    			for (String permString : trueCommand.permissions) { // Do funky stuff
			    				if (!plugin.hasPermissions(event.getPlayer(), permString)) {
			    					if (plugin.Permissions == null) { // BukkitPerms
			    						event.getPlayer().addAttachment(plugin, permString, true, 1);
			    					} else { // Legacy Perms
			    						tempPerms.add(permString);
			    						plugin.Permissions.addUserPermission(event.getClickedBlock().getWorld().getName(), event.getPlayer().getName(), permString);
			    					}
			    				}
			    			}
		    			}
		    			for (String commandString : trueCommand.commands) {
		    				if (commandString.startsWith("/")) {
			    				commandString = commandString.substring(1);
			    			}
			    			event.getPlayer().performCommand(commandString.replace("%p", event.getPlayer().getName()).replace("\\''", "\""));
		    			}
		    			if (plugin.Permissions != null) { // Wipe legacy perms
		    				for (String tempPerm : tempPerms) {
		    					plugin.Permissions.removeUserPermission(event.getClickedBlock().getWorld().getName(), event.getPlayer().getName(), tempPerm);
		    				}
		    			}
	    			} catch (NullPointerException e) {
	    				System.out.println("Could not find commandlink for HiddenCommandSign at " + event.getClickedBlock().getLocation().toString() + " with the sign text " + signText);
						event.getPlayer().sendMessage(ChatColor.RED + "That is a HiddenCommandSign, but the command link could not be found. Perhaps the database file was edited or is missing?");
	    			}
    			}
	    	}
    	}
    }
    
}

