package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

@Value
@ToString
@EqualsAndHashCode
public class BlockInformation {

    Material material;
    BlockState blockState;
}
