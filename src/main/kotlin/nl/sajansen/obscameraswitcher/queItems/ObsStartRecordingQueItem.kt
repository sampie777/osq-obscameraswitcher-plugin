package nl.sajansen.obscameraswitcher.queItems

import objects.OBSClient
import objects.que.QueItem
import nl.sajansen.obscameraswitcher.ObsCameraSwitcherPlugin
import java.awt.Color

class ObsStartRecordingQueItem(override val plugin: ObsCameraSwitcherPlugin) : QueItem {
    override val name: String = "Start recording"
    override var executeAfterPrevious = false
    override var quickAccessColor: Color? = plugin.quickAccessColor

    override fun toString() = name

    override fun activate() {
        OBSClient.getController()!!.startRecording {}
    }
}