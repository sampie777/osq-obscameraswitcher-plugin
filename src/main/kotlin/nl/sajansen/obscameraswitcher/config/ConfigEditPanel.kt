package nl.sajansen.obscameraswitcher.config


import gui.plugins.config.PluginConfigEditPanel
import gui.plugins.config.formInputs.BooleanFormInput
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
                labelText = "Scene containing the cameras",
                allowEmpty = false
            )
        )
        formComponents.add(
            BooleanFormInput(
                "switchUsingVisibility",
                PluginProperties.switchUsingVisibility,
                saveCallback = { value ->
                    logger.info("Saving 'switchUsingVisibility' value: $value")
                    PluginProperties.switchUsingVisibility = value
                },
                labelText = "Switch cameras using visibility instead of order"
            )
        )
    }
}
