package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
/**
 * A Class to represent a block state in the world, except exempt of Bukkit
 * This class is an adapter for the Bukkit BlockState class to abstract the Bukkit API from the rest of the codebase
 */
public class PathBlockState {

    public static final PathBlockState EMPTY_PATH_BLOCK_STATE = new PathBlockState(null);

    private final BlockState blockState;

    public BlockData getBlockData() {
        return this.blockState.getBlockData();
    }

    public MaterialData getData() {
        return this.blockState.getData();
    }

    public byte getLightLevel() {
        return this.blockState.getLightLevel();
    }

    public BlockState getBukkitBlockState() {
        return this.blockState;
    }
}
