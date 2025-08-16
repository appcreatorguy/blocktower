package dev.alphacerium.blocktower

import de.maxhenkel.admiral.MinecraftAdmiral
import dev.alphacerium.blocktower.command.PrivateRoomCommands
import dev.alphacerium.blocktower.config.PrivateRoomStore
import dev.alphacerium.blocktower.events.PrivateRoomEvents
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

object Blocktower : ModInitializer {
    const val MOD_ID = "blocktower"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    lateinit var PRIVATE_ROOM_STORE: PrivateRoomStore

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Hello Fabric world!")
        val configFolder = Paths.get(".", "config").resolve(MOD_ID)
        PRIVATE_ROOM_STORE = PrivateRoomStore(configFolder.resolve("private-rooms.json").toFile())

        CommandRegistrationCallback.EVENT.register{ dispatcher, registryAccess, _ -> MinecraftAdmiral.builder(dispatcher, registryAccess)
            .addCommandClasses(
                PrivateRoomCommands::class.java
            )
            .build()
        }
        PrivateRoomEvents.register()
    }
}