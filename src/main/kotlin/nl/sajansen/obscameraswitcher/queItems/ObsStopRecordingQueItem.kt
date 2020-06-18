package nl.sajansen.obscameraswitcher.queItems

import objects.OBSClient
import objects.que.QueItem
import nl.sajansen.obscameraswitcher.ObsCameraSwitcherPlugin
import java.awt.Color

class ObsStopRecordingQueItem(override val plugin: ObsCameraSwitcherPlugin) : QueItem {
    override val name: String = "Stop recording"
    override var executeAfterPrevious = false
    override var quickAccessColor: Color? = plugin.quickAccessColor

    override fun toString() = name

    override fun activate() {
        OBSClient.getController()!!.stopRecording {}
    }
}