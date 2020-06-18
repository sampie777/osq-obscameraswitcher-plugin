package nl.sajansen.obscameraswitcher

import getCurrentJarDirectory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.logging.Logger

object PluginProperties {
    private val logger = Logger.getLogger(PluginProperties.toString())

    // En-/disables the creation of a properties file and writing to a properties file.
    // Leave disabled when running tests.
    var writeToFile: Boolean = false

    private val propertiesFilePath = getCurrentJarDirectory(this).absolutePath + File.separatorChar + "osq-obscameraswitcher.properties"
    private val properties = Properties()

    var cameraSceneName: String = "Cameras"

    fun load() {
        logger.info("Loading scene timer plugin properties from: $propertiesFilePath")

        if (File(propertiesFilePath).exists()) {
            FileInputStream(propertiesFilePath).use { properties.load(it) }
        } else {
            logger.info("No scene timer plugin properties file found, using defaults")
        }

        cameraSceneName = properties.getProperty("cameraSceneName", cameraSceneName)

        if (!File(propertiesFilePath).exists()) {
            save()
        }
    }

    fun save() {
        logger.info("Saving scene timer plugin properties")
        properties.setProperty("cameraSceneName", cameraSceneName)

        if (!writeToFile) {
            return
        }

        logger.info("Saving to scene timer plugin properties file: $propertiesFilePath")

        FileOutputStream(propertiesFilePath).use { fileOutputStream ->
            properties.store(
                fileOutputStream,
                "User properties for Scene Timer plugin"
            )
        }
    }
}