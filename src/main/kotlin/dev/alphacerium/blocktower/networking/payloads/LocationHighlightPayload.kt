package dev.alphacerium.blocktower.networking.payloads

import dev.alphacerium.blocktower.Blocktower
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

data class LocationHighlightPayload(val blockPos: BlockPos, val color: Int) : CustomPayload {
    companion object {
        val LOCATION_HIGHLIGHT_PAYLOAD_ID: Identifier = Identifier.of(Blocktower.MOD_ID, "location_highlight")
        val ID: CustomPayload.Id<LocationHighlightPayload> = CustomPayload.Id(LOCATION_HIGHLIGHT_PAYLOAD_ID)
        val CODEC: PacketCodec<RegistryByteBuf, LocationHighlightPayload> = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, LocationHighlightPayload::blockPos,
            PacketCodecs.RGB, LocationHighlightPayload::color,
            ::LocationHighlightPayload
        )

        fun register() {
            PayloadTypeRegistry.playS2C().register(ID, CODEC)
        }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return ID
    }
}