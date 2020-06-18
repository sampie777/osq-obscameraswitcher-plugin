package nl.sajansen.obscameraswitcher.config


import gui.plugins.config.PluginConfigEditPanel
import gui.plugins.config.formInputs.StringFormInput
import nl.sajansen.obscameraswitcher.PluginProperties
import java.util.logging.Logger

class ConfigEditPanel : PluginConfigEditPanel() {
    private val logger = Logger.getLogger(ConfigEditPanel::class.java.name)

    override fun createFormInputs() {
        formComponents.add(
            StringFormInput(
                "cameraSceneName",
                PluginProperties.cameraSceneName,
                saveCallback = { value ->
                    logger.info("Saving 'cameraSceneName' value: $value")
                    PluginProperties.cameraSceneName = value
                },
                labelText = "OBS websocket address",
                allowEmpty = false
            )
        )
    }
}
