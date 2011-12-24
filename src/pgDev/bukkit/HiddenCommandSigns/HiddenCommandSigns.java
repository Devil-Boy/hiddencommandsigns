package pgDev.bukkit.HiddenCommandSigns;

import java.io.File;
import java.util.HashMap;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
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
	// Listeners
    private final HiddenCommandSignsPlayerListener playerListener = new HiddenCommandSignsPlayerListener(this);
    private final HiddenCommandSignsBlockListener blockListener = new HiddenCommandSignsBlockListener(this);
    
    // Magic string
    String scsID;
    
    // Permissions support
    private static PermissionHandler Permissions;
    
    // File Locations
    String pluginMainDir = "./plugins/HiddenCommandSigns";
    String pluginConfigLocation = pluginMainDir + "/HiddenCommandSigns.cfg";
    
    // HCS Actions
    public enum signAction { CREATE, DETECT, OBTAINREAL, ADDPERM };
    HashMap<String, signAction> commandUsers = new HashMap<String, signAction>();

    public void onEnable() {
    	// Check for SCS
    	if (!setupSCS()) {
    		System.out.println("SimpleCommandSigns was not found on this server!");
    		getPluginLoader().disablePlugin(this);
    	}

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        
        // Enable output
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
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
				if (args[0].toLowerCase().startsWith("c")) { // Create
					
				} else if (args[0].toLowerCase().startsWith("d")) { // Detect
					
				} else if (args[0].toLowerCase().startsWith("o")) { // ObtainReal
					
				} else if (args[0].toLowerCase().startsWith("a")) { // AddPerm
					
				}
			}
		} else {
			sender.sendMessage("This command is only to be run by a player.");
		}
    	return true;
    }
}

