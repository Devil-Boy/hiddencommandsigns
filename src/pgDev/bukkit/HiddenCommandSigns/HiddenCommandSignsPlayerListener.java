package pgDev.bukkit.HiddenCommandSigns;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

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
	    			
	    			String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
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
		    			event.getPlayer().performCommand(commandString.replace("%p", event.getPlayer().getName()));
	    			}
	    			if (plugin.Permissions != null) { // Wipe legacy perms
	    				for (String tempPerm : tempPerms) {
	    					plugin.Permissions.removeUserPermission(event.getClickedBlock().getWorld().getName(), event.getPlayer().getName(), tempPerm);
	    				}
	    			}
	    		}
	    	}
    	}
    }
    
}

