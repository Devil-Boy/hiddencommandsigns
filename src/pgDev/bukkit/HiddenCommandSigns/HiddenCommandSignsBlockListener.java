package pgDev.bukkit.HiddenCommandSigns;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * FakeCommandSigns block listener
 * @author pgDev
 */
public class HiddenCommandSignsBlockListener extends BlockListener {
    private final HiddenCommandSigns plugin;

    public HiddenCommandSignsBlockListener(final HiddenCommandSigns plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
