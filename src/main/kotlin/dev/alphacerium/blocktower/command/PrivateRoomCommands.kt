@file:Suppress("unused")

package dev.alphacerium.blocktower.command

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Name
import de.maxhenkel.enhancedgroups.EnhancedGroups
import de.maxhenkel.enhancedgroups.config.PersistentGroup
import de.maxhenkel.voicechat.api.Group
import dev.alphacerium.advancedgroups.AdvancedGroupCommands
import dev.alphacerium.blocktower.Blocktower
import dev.alphacerium.blocktower.BlocktowerVoicechatPlugin
import dev.alphacerium.blocktower.config.PrivateRoom
import dev.alphacerium.blocktower.networking.payloads.LocationHighlightPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ColorHelper
import java.util.*

@Command(PrivateRoomCommands.PRIVATE_ROOM_COMMAND)
class PrivateRoomCommands {
    companion object {
        const val PRIVATE_ROOM_COMMAND = "privateroom"
    }

    @Command("add")
    fun add(
        context: CommandContext<ServerCommandSource>,
        @Name("name") name: String,
        @Name("password") password: String,
        @Name("entrance") entrance: BlockPos,
        @Name("exit") exit: BlockPos
    ): Int {
        if (name.isBlank()) {
            context.source.sendError(Text.literal("Name cannot be blank"))
            return 0
        }
        if (AdvancedGroupCommands.PERSISTENT_GROUP_STORE.getGroup(name) == null) {
            // create the group
            val type = Group.Type.NORMAL
            val vcGroup =
                BlocktowerVoicechatPlugin.SERVER_API.groupBuilder().setPersistent(true).setName(name).setType(type)
                    .build()

            val persistentGroup = PersistentGroup(name, password, PersistentGroup.Type.fromGroupType(type), false)
            EnhancedGroups.PERSISTENT_GROUP_STORE.addGroup(persistentGroup)
            EnhancedGroups.PERSISTENT_GROUP_STORE.addCached(vcGroup.id, persistentGroup)
        }

        val persistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(name)!!
        val privateRoom = PrivateRoom(name, entrance, exit, persistentGroup, null)
        Blocktower.PRIVATE_ROOM_STORE.addPrivateRoom(privateRoom)

        context.source.sendFeedback({ Text.literal("Successfully created persistent group $name") }, false)
        return 1
    }

    @Command("remove")
    fun remove(
        context: CommandContext<ServerCommandSource>, @Name("id") id: UUID
    ): Int {
        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(id)
        if (privateRoom == null) {
            context.source.sendError(Text.literal("Private room with id $id not found"))
            return 0
        }
        Blocktower.PRIVATE_ROOM_STORE.removePrivateRoom(privateRoom)
        return 1
    }

    @Command("remove")
    fun remove(
        context: CommandContext<ServerCommandSource>, @Name("name") name: String
    ): Int {
        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(name)
        if (privateRoom == null) {
            context.source.sendError(Text.literal("Private room with name $name not found"))
            return 0
        }
        Blocktower.PRIVATE_ROOM_STORE.removePrivateRoom(privateRoom)
        return 1
    }

    @Command("show")
    fun show(context: CommandContext<ServerCommandSource>, @Name("id") id: UUID): Int {
        if (context.source.world.isClient) {
            return 0 // only run on server
        }

        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(id)
        if (privateRoom == null) {
            context.source.sendError(Text.literal("Private room with id $id not found"))
            return 1
        }

        ServerPlayNetworking.send(
            context.source.player, LocationHighlightPayload(privateRoom.entrance, ColorHelper.getArgb(0, 0, 255))
        )
        ServerPlayNetworking.send(
            context.source.player, LocationHighlightPayload(privateRoom.exit, ColorHelper.getArgb(255, 0, 0))
        )

        context.source.sendFeedback(
            { Text.literal("Highlighted entrance and exit of private room with id $id. Run /$PRIVATE_ROOM_COMMAND show again to remove highlights") },
            false
        )
        return 0
    }

    @Command("show")
    fun show(context: CommandContext<ServerCommandSource>, @Name("name") name: String): Int {
        if (context.source.world.isClient) {
            return 0 // only run on server
        }

        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(name)
        if (privateRoom == null) {
            context.source.sendError(Text.literal("Private room with name $name not found"))
            return 1
        }

        ServerPlayNetworking.send(
            context.source.player, LocationHighlightPayload(privateRoom.entrance, ColorHelper.getArgb(0, 0, 255))
        )
        ServerPlayNetworking.send(
            context.source.player, LocationHighlightPayload(privateRoom.exit, ColorHelper.getArgb(255, 0, 0))
        )

        context.source.sendFeedback(
            { Text.literal("Highlighted entrance and exit of private room with name $name. Run /$PRIVATE_ROOM_COMMAND show again to remove highlights") },
            false
        )
        return 0
    }

    @Command("list")
    fun list(context: CommandContext<ServerCommandSource>): Int {
        val privateRooms = Blocktower.PRIVATE_ROOM_STORE.getPrivateRooms()
        if (privateRooms.isEmpty()) {
            context.source.sendFeedback({ Text.literal("No private rooms found") }, false)
        }
        for (privateRoom in privateRooms) {
            val output = Text.literal(privateRoom.name).append(" ").append(
                Texts.bracketed(Text.literal("Remove")).setStyle(
                    Style.EMPTY.withClickEvent(ClickEvent.RunCommand("/" + PRIVATE_ROOM_COMMAND + " remove " + privateRoom.id))
                        .withHoverEvent(HoverEvent.ShowText(Text.literal("click to delete private room")))
                        .withColor(Formatting.GREEN)
                )
            ).append(" ").append(
                Texts.bracketed(Text.literal("Copy ID")).setStyle(
                    Style.EMPTY.withClickEvent(ClickEvent.CopyToClipboard(privateRoom.id.toString()))
                        .withHoverEvent(HoverEvent.ShowText(Text.literal("click to copy private room id")))
                        .withColor(Formatting.GREEN)
                )
                )
            context.source.sendFeedback({ output }, false)
        }
        return 1
    }
}