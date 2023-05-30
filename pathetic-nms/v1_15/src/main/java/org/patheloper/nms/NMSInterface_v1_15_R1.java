package org.patheloper.nms;

import net.minecraft.server.v1_15_R1.ChunkStatus;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.patheloper.api.snapshot.NMSInterface;

public class NMSInterface_v1_15_R1 implements NMSInterface {

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
