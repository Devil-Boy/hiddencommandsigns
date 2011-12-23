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
public class FakeCommandSignsBlockListener extends BlockListener {
    private final FakeCommandSigns plugin;

    public FakeCommandSignsBlockListener(final FakeCommandSigns plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
