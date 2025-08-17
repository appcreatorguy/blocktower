package dev.alphacerium.blocktower.networking.recievers

import dev.alphacerium.blocktower.BlocktowerClient
import dev.alphacerium.blocktower.networking.payloads.LocationHighlightPayload
import dev.alphacerium.blocktower.records.BlockPosColor
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object LocationHighlightReceiver {
    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(LocationHighlightPayload.ID) { payload, context ->
            context.client().execute {
                if (BlocktowerClient.RENDER_QUEUE.firstOrNull { it.pos.equals(payload.blockPos) } == null) {
                    BlocktowerClient.LOGGER.info("Added location highlight payload ${payload.blockPos}")
                    BlocktowerClient.RENDER_QUEUE.add(BlockPosColor(payload.blockPos, payload.color))
                    BlocktowerClient.LOGGER.info("Current render queue: ${BlocktowerClient.RENDER_QUEUE}")
                } else {
                    BlocktowerClient.LOGGER.info("Removed location highlight payload")
                    BlocktowerClient.RENDER_QUEUE.remove(BlockPosColor(payload.blockPos, payload.color))
                }
            }
        }
    }
}