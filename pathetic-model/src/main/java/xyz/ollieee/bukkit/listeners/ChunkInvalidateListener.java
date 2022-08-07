package xyz.ollieee.bukkit.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import xyz.ollieee.api.snapshot.SnapshotManager;

import java.util.Arrays;

public class ChunkInvalidateListener implements Listener {

    private final SnapshotManager snapshotManager;

    public ChunkInvalidateListener(SnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onFromTo(BlockFromToEvent event) {
        handleEvent(event, event.getToBlock());
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onPistonChange(BlockPistonEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        handleEvent(event, null);
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent event) {
        handleEvent(event, null);
    }

    private void handleEvent(BlockEvent event, Block toBlock) {
        Arrays.asList(toBlock, event.getBlock()).forEach(block -> {
            if (block != null) {
                snapshotManager.InvalidateChunk(block.getWorld().getUID(), block.getChunk().getX(), block.getChunk().getZ());
            }
        });
    }
}
