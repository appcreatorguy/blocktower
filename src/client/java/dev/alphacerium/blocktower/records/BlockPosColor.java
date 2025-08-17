package dev.alphacerium.blocktower.records;

import net.minecraft.util.math.BlockPos;

public record BlockPosColor(
        BlockPos pos,
        int color
) {
}
