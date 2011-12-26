package pgDev.bukkit.HiddenCommandSigns;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

import pgDev.bukkit.HiddenCommandSigns.HiddenCommandSigns.signAction;

/**
 * FakeCommandSigns block listener
 * @author pgDev
 */
public class HiddenCommandSignsBlockListener extends BlockListener {
    private final HiddenCommandSigns plugin;

    public HiddenCommandSignsBlockListener(final HiddenCommandSigns plugin) {
        this.plugin = plugin;
    }
    
    // Check for Sign
    public boolean isSign(Block theBlock) {
    	if (theBlock.getType() == Material.SIGN_POST || theBlock.getType() == Material.WALL_SIGN) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    // Check for CommandSign
    public boolean isCS(Sign mrSign) {
    	return mrSign.getLine(0).equals(ChatColor.GREEN + plugin.scsID);
    }
    
    // Check for either HCS or CS
    public boolean isEitherSign(Sign mrSign) {
    	return mrSign.getLine(0).startsWith(ChatColor.GREEN + plugin.scsID);
    }
    
    // Check for HiddenCommandSign
    public boolean isHCS(Sign mrSign) {
    	return mrSign.getLine(0).equals(ChatColor.GREEN + plugin.scsID + ChatColor.BLUE);
    }

    // Respond to the command actions
    public void onBlockDamage(BlockDamageEvent event) {
    	Player player = event.getPlayer();
    	String name = player.getName();
    	
    	if (plugin.debug) {
    		System.out.println(name + " damaged a block.");
    	}
    	
    	if (plugin.commandUsers.containsKey(name)) {
    		if (plugin.debug) {
        		System.out.println(name + " is using an hcs action.");
        	}
        	
        	// Prevent destroyation
    		event.setCancelled(true);
    		
    		if (plugin.commandUsers.get(name) == signAction.CREATE) {
    			if (plugin.debug) {
    	    		System.out.println(name + " is trying to create a hiddencommandsign");
    	    	}
    			
	    		if (isSign(event.getBlock())) {
	    			if (plugin.debug) {
	    	    		System.out.println("The block " + name + " was a sign.");
	    	    	}
	    			
	    			Sign theSign = (Sign)event.getBlock().getState();
	    			if (isEitherSign(theSign)) {
	    				if (plugin.debug) {
	    		    		System.out.println(name + " hit either an scs or a hcs.");
	    		    	}
	    				
	    				theSign.setLine(0, theSign.getLine(0) + ChatColor.BLUE);
	    				String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
	    				plugin.commandLinks.put(signText, new HiddenCommand(name, plugin.commandData.get(name)));
	    				player.sendMessage(ChatColor.GOLD + "HiddenCommandSign created with " + plugin.commandData.get(name).length + " commands.");
	    				plugin.removeCommandPlayer(player);
	    			} else {
	    				player.sendMessage(ChatColor.RED + "That isn't a CommandSign.");
	    			}
	    		} else {
	    			player.sendMessage(ChatColor.RED + "That isn't a sign.");
	    		}
    		} else if (plugin.commandUsers.get(name) == signAction.ADDPERM) {
    			if (isSign(event.getBlock())) {
    				Sign theSign = (Sign)event.getBlock().getState();
    				if (isHCS(theSign)) {
    					String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
    					plugin.commandLinks.put(signText, plugin.commandLinks.get(signText).addPerms(plugin.commandData.get(name)));
    					plugin.removeCommandPlayer(player);
	    				player.sendMessage(ChatColor.GOLD + "Added " + plugin.commandData.get(name).length + " permission(s) to the HiddenCommandSign.");
    				} else {
    					player.sendMessage(ChatColor.RED + "That isn't a HiddenCommandSign.");
    				}
    			} else {
    				player.sendMessage(ChatColor.RED + "That isn't a sign.");
    			}
    		}
    	}
    }
}
