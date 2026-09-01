package video.api.flutter.livestream

import io.github.thibaultbee.streampack.core.elements.sources.video.camera.CameraSettings
import io.github.thibaultbee.streampack.core.elements.sources.video.camera.ICameraSource
import kotlinx.coroutines.runBlocking
import video.api.flutter.livestream.generated.CameraSettingsHostApi
import video.api.flutter.livestream.manager.InstanceManager

class CameraSettingsHostApiImpl(
    private val instanceManager: InstanceManager
) :
    CameraSettingsHostApi {
    private val settings: CameraSettings
        get() {
            val streamer = runBlocking { instanceManager.getInstance() }
            return (streamer.videoInput.sourceFlow.value as? ICameraSource)?.settings
                ?: throw IllegalStateException("Camera source is not available")
        }

    override fun setZoomRatio(zoomRatio: Double) {
        runBlocking { settings.zoom.setZoomRatio(zoomRatio.toFloat()) }
    }

    override fun getZoomRatio(): Double {
        return runBlocking { settings.zoom.getZoomRatio().toDouble() }
    }
}
