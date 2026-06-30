package com.simonov.skinvault

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

object SkinStorage {

    private val logger = LoggerFactory.getLogger("SkinVault/Storage")
    private val json   = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val configDir = FabricLoader.getInstance().configDir.resolve("skinvault")
    private val dataFile  = configDir.resolve("skins.json")

    // ── Versioned wrapper — bump CURRENT_VERSION when the schema changes ──
    private const val CURRENT_VERSION = 1

    @Serializable
    private data class StorageFile(
        val version: Int,
        val skins: List<SkinEntry>
    )

    @Serializable
    data class SkinEntry(
        val name: String,
        val url: String
    )

    // ── Public API ────────────────────────────────────────────────────────

    fun load(): MutableList<SkinEntry> {
        if (!dataFile.toFile().exists()) return mutableListOf()

        return try {
            val file = json.decodeFromString<StorageFile>(dataFile.toFile().readText())
            migrate(file).skins.toMutableList()
        } catch (e: Exception) {
            logger.error("Failed to load skins.json, starting fresh: ${e.message}")
            mutableListOf()
        }
    }

    fun save(skins: List<SkinEntry>) {
        try {
            configDir.toFile().mkdirs()
            val file = StorageFile(version = CURRENT_VERSION, skins = skins)
            dataFile.toFile().writeText(json.encodeToString(file))
        } catch (e: Exception) {
            logger.error("Failed to save skins.json: ${e.message}")
        }
    }

    // ── Migration ladder — add a branch here for each version bump ────────
    private fun migrate(file: StorageFile): StorageFile {
        var current = file

        // v0 → v1: nothing to migrate yet, placeholder for future changes
        // if (current.version < 1) {
        //     current = current.copy(version = 1, skins = current.skins.map { ... })
        // }

        if (current.version > CURRENT_VERSION) {
            logger.warn("skins.json is version ${current.version}, storage is $CURRENT_VERSION — some data may be ignored")
        }

        return current
    }
}

