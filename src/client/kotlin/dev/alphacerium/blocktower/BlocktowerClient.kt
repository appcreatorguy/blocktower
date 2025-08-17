package dev.alphacerium.blocktower

import dev.alphacerium.blocktower.Blocktower.MOD_ID
import dev.alphacerium.blocktower.events.ClientBlockPosHighlighterEvents
import dev.alphacerium.blocktower.networking.recievers.LocationHighlightReceiver
import dev.alphacerium.blocktower.records.BlockPosColor
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object BlocktowerClient : ClientModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    @JvmField
    val RENDER_QUEUE: MutableSet<BlockPosColor> = Collections.synchronizedSet(mutableSetOf<BlockPosColor>())
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        LocationHighlightReceiver.register()
        ClientBlockPosHighlighterEvents.register()
	}
}