package nl.sajansen.obscameraswitcher

import java.util.*

object PluginInfo {
    private val properties = Properties()
    val version: String
    val author: String

    init {
        properties.load(javaClass.getResourceAsStream("info.properties"))
        version = properties.getProperty("version", "unknown")
        author = properties.getProperty("author", "Samuel-Anton Jansen")
    }
}