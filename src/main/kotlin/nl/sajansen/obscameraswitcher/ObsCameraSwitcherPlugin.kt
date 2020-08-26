package nl.sajansen.obscameraswitcher

import GUI
import gui.Refreshable
import gui.plugins.config.ConfigActionPanel
import gui.plugins.config.ConfigWindow
import gui.utils.DefaultSourcesList
import gui.utils.createImageIcon
import gui.utils.getMainFrameComponent
import gui.utils.getMainMenu
import net.twasi.obsremotejava.requests.ReorderSceneItems.ReorderSceneItemsRequest
import nl.sajansen.obscameraswitcher.config.ConfigEditPanel
import nl.sajansen.obscameraswitcher.queItems.ObsCameraSwitchQueItem
import objects.OBSClient
import objects.OBSState
import objects.TScene
import objects.notifications.Notifications
import objects.que.JsonQueue
import objects.que.QueItem
import plugins.common.QueItemBasePlugin
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.util.logging.Logger
import javax.swing.*
import javax.swing.border.EmptyBorder

@Suppress("unused")
class ObsCameraSwitcherPlugin : QueItemBasePlugin, Refreshable {

    private val logger = Logger.getLogger(ObsCameraSwitcherPlugin::class.java.name)

    override val name = "ObsCameraSwitcherPlugin"
    override val description = "Plugin to switch cameras in OBS"
    override val version: String = PluginInfo.version

    override val icon: Icon? = createImageIcon("/plugins/obs/icon-14.png")

    override val tabName = "OBS Camera Switcher"

    internal val quickAccessColor = Color(255, 229, 229)
    private val cameraSourcesList: JList<ObsCameraSwitchQueItem> = DefaultSourcesList()
    private var cameraScene: TScene? = null

    override fun enable() {
        super.enable()
        PluginProperties.writeToFile = true
        PluginProperties.load()
        GUI.register(this)
    }

    override fun disable() {
        super.disable()
        GUI.unregister(this)
        PluginProperties.save()
    }

    override fun createMenu(menu: JMenu): Boolean {
        val settingsItem = JMenuItem("Settings")
        settingsItem.addActionListener {
            ConfigWindow(
                    getMainFrameComponent(getMainMenu(menu)),
                    "OBS Camera Switcher Plugin Settings",
                    ConfigEditPanel(),
                    ConfigActionPanel(null) { PluginProperties.save() }
            )
        }
        menu.add(settingsItem)
        return true
    }

    override fun sourcePanel(): JComponent {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = EmptyBorder(10, 10, 0, 10)

        val titleLabel = JLabel("Available cameras")
        panel.add(titleLabel, BorderLayout.PAGE_START)

        val scrollPanel = JScrollPane(cameraSourcesList)
        scrollPanel.preferredSize = Dimension(300, 500)
        scrollPanel.border = BorderFactory.createLineBorder(Color(180, 180, 180))
        panel.add(scrollPanel, BorderLayout.CENTER)

        return panel
    }

    override fun configStringToQueItem(value: String): QueItem {
        TODO("Not implemented")
    }

    override fun jsonToQueItem(jsonQueItem: JsonQueue.QueueItem): QueItem {
        return when (jsonQueItem.className) {
            ObsCameraSwitchQueItem::class.java.simpleName -> ObsCameraSwitchQueItem.fromJson(this, jsonQueItem)
            else -> throw IllegalArgumentException("Invalid OBS Camera Switcher queue item: ${jsonQueItem.className}")
        }
    }

    override fun refreshScenes() {
        cameraScene = OBSState.scenes.find { it.name == PluginProperties.cameraSceneName }
        if (cameraScene == null) {
            logger.info("Could not find camera scene: ${PluginProperties.cameraSceneName}")
            Notifications.add("Could not find camera scene: ${PluginProperties.cameraSceneName}", "OBS Camera Switcher")
            return
        }

        cameraSourcesList.setListData(cameraScene!!.sources.map { ObsCameraSwitchQueItem(this, cameraScene!!, it) }
                .toTypedArray())
        cameraSourcesList.repaint()
    }

    fun hideAllCameras(exceptSourceName: String? = null) {
        val controller = OBSClient.getController()
        if (controller == null) {
            logger.info("OBS controller is null, cannot activate Camera Switch Queue Item")
            Notifications.add("Cannot switch camera. Make sure OBS is connected.", "OBS Camera Switcher")
            return
        }

        if (cameraScene == null) {
            logger.info("Camera scene not loaded")
            Notifications.add("Camera scene not loaded", "OBS Camera Switcher")
            return
        }

        cameraScene!!.sources
                .filter { it.name != exceptSourceName }
                .forEach {
                    controller.setSourceVisibility(PluginProperties.cameraSceneName, it.name, false) {}
                }
    }

    fun activateCamera(sourceName: String) {
        val controller = OBSClient.getController()
        if (controller == null) {
            logger.info("OBS controller is null, cannot activate Camera Switch Queue Item")
            Notifications.add("Cannot switch camera. Make sure OBS is connected.", "OBS Camera Switcher")
            return
        }

        if (PluginProperties.switchUsingVisibility) {
            controller.setSourceVisibility(PluginProperties.cameraSceneName, sourceName, true) {
                hideAllCameras(exceptSourceName = sourceName)
            }
        } else {
            val cameraSource = cameraScene!!.sources.find { it.name == sourceName }
            if (cameraSource == null) {
                Notifications.add("Cannot find camera '$sourceName' in scene sources", "OBS Camera Switcher")
                return
            }

            val sources = listOf(cameraSource) + cameraScene!!.sources.filter { it != cameraSource }

            val obsItems = sources.map { ReorderSceneItemsRequest(null, null, null).Item(null, it.name) }

            controller.reorderSceneItems(PluginProperties.cameraSceneName, obsItems) {}
        }
    }
}