package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

import javax.annotation.Nullable;

@Value
@ToString
@EqualsAndHashCode
public class BlockInformation {

    @NonNull
    Material material;
    @Nullable // in case pathetic is used in v. 1.13 or below
    BlockState blockState;
}
