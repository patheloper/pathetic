package xyz.ollieee.nms.v1_16;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import xyz.ollieee.api.snapshot.ChunkSnapshotGrabber;

public class OneSixteenSnapshotGrabber implements ChunkSnapshotGrabber {

    @Override
    public ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ) {
        try {

            WorldServer server = ((CraftWorld) world).getHandle();
            CraftChunk newCraftChunk = ((CraftChunk) world.getChunkAt(chunkX, chunkZ));

            server.getChunkProvider().getChunkAt(chunkX, chunkZ, ChunkStatus.FULL, true);

            return newCraftChunk.getChunkSnapshot();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
