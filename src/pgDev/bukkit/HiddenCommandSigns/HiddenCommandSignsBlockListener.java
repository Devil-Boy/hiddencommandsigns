package pgDev.bukkit.HiddenCommandSigns;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;

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
    @EventHandler
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
	    				
	    				if (isCS(theSign)) { // Convert the Sign to HCS
	    					theSign.setLine(0, theSign.getLine(0) + ChatColor.BLUE);
	    					theSign.update();
	    				}
	    				String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
	    				plugin.commandLinks.put(signText, new HiddenCommand(name, plugin.commandData.get(name)));
	    				plugin.saveDB();
	    				player.sendMessage(ChatColor.GOLD + "HiddenCommandSign created with " + plugin.commandData.get(name).length + " commands.");
	    			} else {
	    				player.sendMessage(ChatColor.RED + "That isn't a CommandSign.");
	    			}
	    		} else {
	    			player.sendMessage(ChatColor.RED + "That isn't a sign.");
	    		}
    		} else if (plugin.commandUsers.get(name) == signAction.DETECT) {
    			if (isSign(event.getBlock())) {
    				Sign theSign = (Sign)event.getBlock().getState();
    				if (isHCS(theSign)) {
    					String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
    					try {
    						player.sendMessage(ChatColor.GOLD + "HiddenCommandSign created by " + plugin.commandLinks.get(signText).author);
    					} catch (NullPointerException e) {
    						System.out.println("Could not find commandlink for HiddenCommandSign at " + event.getBlock().getLocation().toString() + " with the sign text " + signText);
    						player.sendMessage(ChatColor.RED + "That is a HiddenCommandSign, but the command link could not be found. Perhaps the database file was edited or is missing?");
    					}
    				} else {
    					player.sendMessage(ChatColor.RED + "That isn't a HiddenCommandSign.");
    				}
    			} else {
    				player.sendMessage(ChatColor.RED + "That isn't a sign.");
    			}
    		} else if (plugin.commandUsers.get(name) == signAction.OBTAINREAL) {
    			if (isSign(event.getBlock())) {
    				Sign theSign = (Sign)event.getBlock().getState();
    				if (isHCS(theSign)) {
    					String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
    					try {
    						String commandSequence = "";
    						for (String commandString : plugin.commandLinks.get(signText).commands) {
    							if (commandSequence.equals("")) {
    								commandSequence = "\"" + commandString + "\"";
    							} else {
    								commandSequence = commandSequence + " \"" + commandString + "\"";
    							}
    						}
    						player.sendMessage(ChatColor.GOLD + "Command(s): " + commandSequence);
    						if (plugin.commandLinks.get(signText).permissions != null) {
    							String permList = "";
    							for (String permString : plugin.commandLinks.get(signText).permissions) {
    								if (permList.equals("")) {
    									permList = "\"" + permString + "\"";
        							} else {
        								permList = permList + " \"" + permString + "\"";
        							}
    							}
    							player.sendMessage(ChatColor.GOLD + "Permission(s): " + permList);
    						}
    					} catch (NullPointerException e) {
    						System.out.println("Could not find commandlink for HiddenCommandSign at " + event.getBlock().getLocation().toString() + " with the sign text " + signText);
    						player.sendMessage(ChatColor.RED + "That is a HiddenCommandSign, but the command link could not be found. Perhaps the database file was edited or is missing?");
    					}
    				} else {
    					player.sendMessage(ChatColor.RED + "That isn't a HiddenCommandSign.");
    				}
    			} else {
    				player.sendMessage(ChatColor.RED + "That isn't a sign.");
    			}
    		} else if (plugin.commandUsers.get(name) == signAction.ADDPERM) {
    			if (isSign(event.getBlock())) {
    				Sign theSign = (Sign)event.getBlock().getState();
    				if (isHCS(theSign)) {
    					String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
    					try {
	    					plugin.commandLinks.put(signText, plugin.commandLinks.get(signText).addPerms(plugin.commandData.get(name)));
	    					plugin.saveDB();
		    				player.sendMessage(ChatColor.GOLD + "Added " + plugin.commandData.get(name).length + " permission(s) to the HiddenCommandSign.");
    					} catch (NullPointerException e) {
    						System.out.println("Could not find commandlink for HiddenCommandSign at " + event.getBlock().getLocation().toString() + " with the sign text " + signText);
    						player.sendMessage(ChatColor.RED + "That is a HiddenCommandSign, but the command link could not be found. Perhaps the database file was edited or is missing?");
    					}
    				} else {
    					player.sendMessage(ChatColor.RED + "That isn't a HiddenCommandSign.");
    				}
    			} else {
    				player.sendMessage(ChatColor.RED + "That isn't a sign.");
    			}
    		} else if (plugin.commandUsers.get(name) == signAction.QUICKCREATE) {
    			if (isSign(event.getBlock())) {
    				Sign theSign = (Sign)event.getBlock().getState();
    				if (isCS(theSign)) {
    					String signText = theSign.getLine(1) + theSign.getLine(2) + theSign.getLine(3);
    					if (plugin.commandLinks.containsKey(signText)) {
    						theSign.setLine(0, theSign.getLine(0) + ChatColor.BLUE);
    						player.sendMessage(ChatColor.GOLD + "Sign converted using pre-set command(s).");
    					} else {
    						player.sendMessage(ChatColor.RED + "There are no commands set for the text on that sign.");
    					}
    				} else if (isHCS(theSign)) {
    					player.sendMessage(ChatColor.RED + "That is already a hiddencommandsign.");
    				} else {
    					player.sendMessage(ChatColor.RED + "That isn't a commandsign.");
    				}
    			} else {
    				player.sendMessage(ChatColor.RED + "That isn't a sign.");
    			}
    		}
    		plugin.removeCommandPlayer(player);
    	}
    }
}
