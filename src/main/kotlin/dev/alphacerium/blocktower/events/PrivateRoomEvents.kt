package dev.alphacerium.blocktower.events

import dev.alphacerium.advancedgroups.core.PushGroup
import dev.alphacerium.advancedgroups.core.ReleaseGroup
import dev.alphacerium.blocktower.Blocktower
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerLevel

object PrivateRoomEvents {
    fun register() {
        ServerTickEvents.END_WORLD_TICK.register { world ->
            checkPlayers(world)
        }
    }

    private fun checkPlayers(world: ServerLevel) {
        val rooms = Blocktower.PRIVATE_ROOM_STORE.getPrivateRooms()
        if (rooms.isEmpty()) return

        for (player in world.players()) {
            val pos = player.blockPosition()
            for (room in rooms) {
                when (pos) {
                    // TODO: fix this so it works like the datapack
                    room.entrance -> {
                        Blocktower.LOGGER.info("Player ${player.gameProfile.name} is at entrance of private room ${room.name}")
                        PushGroup.pushGroup(player, room.group.id)
                    }
                    room.exit -> {
                        Blocktower.LOGGER.info("Player ${player.gameProfile.name} is at exit of private room ${room.name}")
                        ReleaseGroup.releaseGroup(player)
                    }
                    else -> {}
                }
            }
        }
    }
}