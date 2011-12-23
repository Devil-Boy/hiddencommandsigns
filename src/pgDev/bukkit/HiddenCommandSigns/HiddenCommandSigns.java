package pgDev.bukkit.HiddenCommandSigns;

import java.io.File;
import java.util.HashMap;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
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
    String pluginMainDir = "./plugins/SimpleCommandSigns";
    String pluginConfigLocation = pluginMainDir + "/SimpleCommandSigns.cfg";

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
        System.out.println("FakeCommandSigns disabled!");
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
        if (Permissions != null) {
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
		} else {
			sender.sendMessage("This command is only to be run by a player.");
		}
    	return true;
    }
}

