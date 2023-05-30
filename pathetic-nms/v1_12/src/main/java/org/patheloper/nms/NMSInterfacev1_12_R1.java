package org.patheloper.nms;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.patheloper.api.snapshot.NMSInterface;

public class NMSInterfacev1_12_R1 implements NMSInterface {

    @Override
    public ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ) {
        try {
            WorldServer server = ((CraftWorld) world).getHandle();
            Chunk chunk = server.getChunkProvider().getChunkAt(chunkX, chunkZ);

            return chunk.bukkitChunk.getChunkSnapshot();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
