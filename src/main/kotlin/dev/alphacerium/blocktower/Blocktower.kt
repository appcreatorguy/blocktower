package dev.alphacerium.blocktower

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

object Blocktower : ModInitializer {
    const val MOD_ID = "blocktower"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!")
        val configFolder = Paths.get(".","config").resolve(MOD_ID)
	}
}