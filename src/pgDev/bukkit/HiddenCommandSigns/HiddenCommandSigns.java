package pgDev.bukkit.HiddenCommandSigns;

import java.io.File;
import java.util.*;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import pgDev.bukkit.SimpleCommandSigns.SimpleCommandSigns;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * FakeCommandSigns for Bukkit
 *
 * @author pgDev
 */
public class HiddenCommandSigns extends JavaPlugin {
	// Debug output?
	public boolean debug = false;
	
	// Listeners
    final HiddenCommandSignsPlayerListener playerListener = new HiddenCommandSignsPlayerListener(this);
    final HiddenCommandSignsBlockListener blockListener = new HiddenCommandSignsBlockListener(this);
    
    // Magic string
    String scsID;
    
    // Permissions support
    static PermissionHandler Permissions;
    
    // File Locations
    String pluginMainDir = "./plugins/HiddenCommandSigns";
    String pluginConfigLocation = pluginMainDir + "/HiddenCommandSigns.cfg";
    String commandDBLocation = pluginMainDir + "/HiddenCommands.ini";
    
    // HCS Actions
    public enum signAction { CREATE, DETECT, OBTAINREAL, ADDPERM, QUICKCREATE };
    HashMap<String, signAction> commandUsers = new HashMap<String, signAction>();
    HashMap<String, String[]> commandData = new HashMap<String, String[]>(); // For CREATE and ADDPERM
    
    // True Command Database
    HashMap<String, HiddenCommand> commandLinks = new HashMap<String, HiddenCommand>();

    public void onEnable() {
    	// Check for SCS
    	if (!setupSCS()) {
    		System.out.println("SimpleCommandSigns was not found on this server!");
    		getPluginLoader().disablePlugin(this);
    	} else {
    		// Check for the plugin directory (create if it does not exist)
        	File pluginDir = new File(pluginMainDir);
    		if(!pluginDir.exists()) {
    			boolean dirCreation = pluginDir.mkdirs();
    			if (dirCreation) {
    				System.out.println("New HiddenCommandSigns directory created!");
    			}
    		}
    		
    		// Load up database (if there isn't one it's fine)
    		if ((new File(commandDBLocation)).exists()) {
    			if (HCSDatabaseIO.checkIntegrity(commandDBLocation)) {
    				commandLinks = HCSDatabaseIO.getDB(commandDBLocation);
    			} else {
    				System.out.println("HiddenCommandSigns database corrupted. Did you change the line positions?");
    				getPluginLoader().disablePlugin(this);
    			}
    		}
    		
    		// Legacy Permissions set up
    		setupPermissions();
    		
	        // Register our events
	        PluginManager pm = getServer().getPluginManager();
	        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
	        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Normal, this);
	        
	        // Enable output
	        PluginDescriptionFile pdfFile = this.getDescription();
	        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    	}
    }
    
    public void onDisable() {
        System.out.println("HiddenCommandSigns disabled!");
    }
    
    // SCS Link
    public boolean setupSCS() {
    	Plugin scs = this.getServer().getPluginManager().getPlugin("SimpleCommandSigns");

        if (scs != null) {
        	scsID = ((SimpleCommandSigns) scs).pluginSettings.commandSignIdentifier;
            return true;
        } else {
        	return false;
        }
    }
    
    // Permissions Methods
    private void setupPermissions() {
        Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (permissions != null) {
                Permissions = ((Permissions)permissions).getHandler();
            } else {
            }
        }
    }
    
    public static boolean hasPermissions(Player player, String node) {
        if (Permissions != null) { // .addUserPermission and .removeUserPermission: world, user, node
        	return Permissions.has(player, node);
        } else {
            return player.hasPermission(node);
        }
    }
    
    // Double database handling
    public void addCommandPlayer(Player thePlayer, signAction leActione, String[] commandStuff) {
    	String hisName = thePlayer.getName();
		commandUsers.put(hisName, leActione);
		if (commandStuff == null) {
			commandData.remove(hisName);
		} else {
			commandData.put(hisName, commandStuff);
		}
    }
    public void removeCommandPlayer(Player thePlayer) {
    	String hisName = thePlayer.getName();
    	commandData.remove(hisName);
    	commandUsers.remove(hisName);
    }
    
    // Save CL Database
    public void saveDB() {
    	HCSDatabaseIO.saveDB(commandDBLocation, commandLinks);
    }
    
    // Handle commands
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
			
			// Start here
			if (args.length == 0) { // Help output
				if (hasPermissions(player, "hcs.create") || hasPermissions(player, "hcs.detect") || hasPermissions(player, "hcs.obtainreal")) {
					player.sendMessage(ChatColor.GREEN + "Useable HiddenCommandSignsCommands:");
					if (hasPermissions(player, "hcs.create")) {
						player.sendMessage(ChatColor.GREEN + "/hcs create \"<true command>\" [\"other commands\"]");
						player.sendMessage(ChatColor.GREEN + "/hcs addperm \"<permission>\" [\"other permissions\"]");
					}
					if (hasPermissions(player, "hcs.detect")) {
						player.sendMessage(ChatColor.GREEN + "/hcs detect");
					}
					if (hasPermissions(player, "hcs.obtainreal")) {
						player.sendMessage(ChatColor.GREEN + "/hcs obtainreal");
					}
					player.sendMessage(ChatColor.GREEN + "Note: Only the first letter of the command will work as well.");
				} else {
					player.sendMessage(ChatColor.RED + "You do not have the permissions required to run any HiddenCommandSigns command.");
				}
			} else {
				if (args[0].toLowerCase().matches("[a-z]{1,}")) { // Only letters
					// Convert arguments into a string I might be able to parse
					String argString = "";
					for (String arg : args) {
						if (argString == "") {
							argString = arg;
						} else {
							argString = argString + " " + arg;
						}
					}
					
					// React to the commands
					if (args[0].toLowerCase().startsWith("c")) { // Create
						if (hasPermissions(player, "hcs.create")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.GREEN + "Usage: /hcs create \"<command>\" [\"othercommand\"]");
							} else {
								// Fix the input to our needs
								String modifiedArgString = argString.replace(args[0], "").trim();
								if (modifiedArgString.startsWith("\"")) {
									modifiedArgString = modifiedArgString.substring(1);
								}
								if (modifiedArgString.endsWith("\"")) {
									modifiedArgString = modifiedArgString.substring(0, modifiedArgString.length() - 1);
								}
								String[] commandSequence = modifiedArgString.split("\" \"");
								
								// Set him up for the hitting
								addCommandPlayer(player , signAction.CREATE, commandSequence);
								
								// Tell him what to do
								player.sendMessage(ChatColor.BLUE + "Left-click the sign you wish to convert.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You do not have the permission required to run this command.");
						}
						
					} else if (args[0].toLowerCase().startsWith("d")) { // Detect
						if (hasPermissions(player, "hcs.detect")) {
							addCommandPlayer(player , signAction.DETECT, null);
							player.sendMessage(ChatColor.BLUE + "Left-click the sign you wish to check.");
						} else {
							player.sendMessage(ChatColor.RED + "You do not have the permission required to run this command.");
						}
						
					} else if (args[0].toLowerCase().startsWith("o")) { // ObtainReal
						if (hasPermissions(player, "hcs.obtainreal")) {
							addCommandPlayer(player, signAction.OBTAINREAL, null);
							player.sendMessage(ChatColor.BLUE + "Left-click the sign you wish to obtain the hidden command of.");
						} else {
							player.sendMessage(ChatColor.RED + "You do not have the permission required to run this command.");
						}
						
					} else if (args[0].toLowerCase().startsWith("a")) { // AddPerm
						if (hasPermissions(player, "hcs.addperm")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.GREEN + "Usage: /hcs addperm \"<permission>\" [\"otherpermission\"]");
							} else {
								// Fix the input to our needs
								String modifiedArgString = argString.replace(args[0], "").trim();
								if (modifiedArgString.startsWith("\"")) {
									modifiedArgString = modifiedArgString.substring(1);
								}
								if (modifiedArgString.endsWith("\"")) {
									modifiedArgString = modifiedArgString.substring(0, modifiedArgString.length() - 1);
								}
								String[] permSequence = modifiedArgString.split("\" \"");
								
								// Set him up for the hitting
								addCommandPlayer(player , signAction.ADDPERM, permSequence);
								
								// Tell him what to do
								player.sendMessage(ChatColor.BLUE + "Left-click the sign you wish to add the permission(s) to.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You do not have the permission required to run this command.");
						}
					} else if (args[0].toLowerCase().startsWith("q")) { // QuickCreate
						if (hasPermissions(player, "hcs.create")) {
							// Set him up for the hitting
							addCommandPlayer(player , signAction.QUICKCREATE, null);
							
							// Tell him what to do
							player.sendMessage(ChatColor.BLUE + "Left-click the sign you wish to convert into a hiddencommandsign.");
						} else {
							player.sendMessage(ChatColor.RED + "You do not have the permission required to run this command.");
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "I know I said the command only needs the first letter to work, but you didn't need to type THAT...");
				}
			}
		} else {
			sender.sendMessage("This command is only to be run by a player.");
		}
    	return true;
    }
}

