package dev.alphacerium.blocktower.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.maxhenkel.enhancedgroups.EnhancedGroups
import de.maxhenkel.enhancedgroups.config.PersistentGroup
import dev.alphacerium.blocktower.Blocktower
import dev.alphacerium.blocktower.BlocktowerVoicechatPlugin
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class PrivateRoomStore(private val file: File) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var rooms: MutableList<PrivateRoom> = mutableListOf()

    init {
        load()
    }

    fun load() {
        if (!file.exists()) {
            Blocktower.LOGGER.error("Private room store file not found")
            return // don't make a file if we don't have to
        }
        try {
            FileReader(file).use { reader ->
                val roomListType = object: TypeToken<MutableList<PrivateRoom>>() {}.type
                rooms = gson.fromJson(reader, roomListType)
            }
        } catch (e: Exception) {
            Blocktower.LOGGER.error("Failed to parse private room store file", e)
        }
        rooms.forEach { it.id } // generate ids for rooms that have none
        save()
    }

    fun save() {
        file.parentFile.mkdirs()
        try {
            FileWriter(file).use { writer ->
                gson.toJson(rooms, writer)
            }
        } catch (e: Exception) {
            Blocktower.LOGGER.error("Failed to save private rooms", e)
        }
    }

    fun getPrivateRoom(name: String): PrivateRoom? {
        return rooms.firstOrNull { it.name.trim() == name.trim() }
    }

    fun getPrivateRoom(id: UUID): PrivateRoom? {
        return rooms.firstOrNull { it.id == id }
    }

    fun getPrivateRooms(): List<PrivateRoom> {
        return rooms
    }

    fun addPrivateRoom(privateRoom: PrivateRoom) {
        rooms.add(privateRoom)
        save()
    }

    fun removePrivateRoom(privateRoom: PrivateRoom) {
        removePersistentGroup(privateRoom.group)
        rooms.remove(privateRoom)
        save()
    }

    fun removePersistentGroup(persistentGroup: PersistentGroup) {
        val voicechatId = EnhancedGroups.PERSISTENT_GROUP_STORE.getVoicechatId(persistentGroup.id)
        val group = BlocktowerVoicechatPlugin.SERVER_API.getGroup(voicechatId)
        if (group == null) {
            Blocktower.LOGGER.error("Group with id $voicechatId and name ${persistentGroup.name} not found")
            return
        }
        val removed = BlocktowerVoicechatPlugin.SERVER_API.removeGroup(voicechatId)
        if (removed) {
            EnhancedGroups.PERSISTENT_GROUP_STORE.removeGroup(persistentGroup)
            Blocktower.LOGGER.info("Removed group with id $voicechatId and name ${persistentGroup.name}")
        } else {
            Blocktower.LOGGER.error("Failed to remove group with id $voicechatId and name ${persistentGroup.name}")
        }
    }
}