package dev.alphacerium.blocktower.config

import de.maxhenkel.enhancedgroups.config.PersistentGroup
import net.minecraft.util.math.BlockPos
import java.util.UUID

class PrivateRoom(
    val name: String,
    val entrance: BlockPos,
    val exit: BlockPos,
    val group: PersistentGroup,
    id: UUID?
) {
    val id: UUID = id ?: UUID.randomUUID()
}