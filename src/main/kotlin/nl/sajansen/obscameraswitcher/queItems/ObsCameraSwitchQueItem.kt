package nl.sajansen.obscameraswitcher.queItems

import objects.OBSClient
import objects.OBSState
import objects.TScene
import objects.que.Que
import objects.que.QueItem
import nl.sajansen.obscameraswitcher.ObsCameraSwitcherPlugin
import objects.TSource
import objects.notifications.Notifications
import objects.que.JsonQue
import themes.Theme
import java.awt.Color
import java.util.logging.Logger
import javax.swing.JLabel

class ObsCameraSwitchQueItem(override val plugin: ObsCameraSwitcherPlugin, val scene: TScene, val source: TSource) :
    QueItem {

    private val logger = Logger.getLogger(ObsCameraSwitchQueItem::class.java.name)

    companion object {
        fun fromJson(plugin: ObsCameraSwitcherPlugin, jsonQueItem: JsonQue.QueItem): ObsCameraSwitchQueItem {
            val scene = TScene(jsonQueItem.data["scene"])
            val source = TSource(jsonQueItem.data["source"]!!)
            val queItem = ObsCameraSwitchQueItem(plugin, scene, source)
            queItem.dataFromJson(jsonQueItem)
            return queItem
        }
    }

    override val name: String = source.name
    override var executeAfterPrevious = false
    override var quickAccessColor: Color? = plugin.quickAccessColor

    override fun toString(): String = renderText()

    override fun toJson(): JsonQue.QueItem {
        val jsonQueItem = super.toJson()
        jsonQueItem.data["scene"] = scene.name
        jsonQueItem.data["source"] = source.name
        return jsonQueItem
    }

    override fun activate() {
        val controller = OBSClient.getController()
        if (controller == null) {
            logger.info("OBS controller is null, cannot activate Camera Switch Queue Item")
            Notifications.add("Cannot switch camera. Make sure OBS is connected.", "OBS Camera Switcher")
            return
        }

        controller.setSourceVisibility(scene.name, source.name, true) {
            plugin.hideAllCameras(exceptSource = source)
        }
    }
}