package nl.sajansen.obscameraswitcher

import handles.QueItemTransferHandler
import nl.sajansen.obscameraswitcher.queItems.ObsStartRecordingQueItem
import nl.sajansen.obscameraswitcher.queItems.ObsStartStreamingQueItem
import nl.sajansen.obscameraswitcher.queItems.ObsStopRecordingQueItem
import nl.sajansen.obscameraswitcher.queItems.ObsStopStreamingQueItem
import java.awt.datatransfer.Transferable
import javax.swing.JComponent
import javax.swing.JList

class ObsActionQueItemTransferHandler(private val plugin: ObsCameraSwitcherPlugin) : QueItemTransferHandler() {

    override fun createTransferable(component: JComponent): Transferable {
        val list = component as JList<*>

        queItem = when (list.selectedValue) {
            is ObsStartRecordingQueItem -> ObsStartRecordingQueItem(plugin)
            is ObsStartStreamingQueItem -> ObsStartStreamingQueItem(plugin)
            is ObsStopRecordingQueItem -> ObsStopRecordingQueItem(plugin)
            is ObsStopStreamingQueItem -> ObsStopStreamingQueItem(plugin)
            else -> null
        }

        return super.createTransferable(component)
    }
}