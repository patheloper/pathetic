package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.bukkit.block.BlockState;

/**
 * A Class to represent a block state in the world, except exempt of Bukkit
 * This class is an adapter for the Bukkit BlockState class to abstract the Bukkit API from the rest of the codebase
 */
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class PathBlockState {

    public static final PathBlockState EMPTY_PATH_BLOCK_STATE = new PathBlockState(null);

    /*
     * This will cause a double method call to get the block state, but to keep our api clean from bukkit,
     * we have to die this death.
     */
    private final BlockState blockState;
}
