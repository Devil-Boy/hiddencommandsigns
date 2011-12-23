package pgDev.bukkit.HiddenCommandSigns;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author pgDev
 */
public class FakeCommandSignsPlayerListener extends PlayerListener {
    private final FakeCommandSigns plugin;

    public FakeCommandSignsPlayerListener(FakeCommandSigns instance) {
        plugin = instance;
    }
    
}

