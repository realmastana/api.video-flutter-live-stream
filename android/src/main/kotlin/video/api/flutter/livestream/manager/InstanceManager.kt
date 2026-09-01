package video.api.flutter.livestream.manager

import android.content.Context
import io.github.thibaultbee.streampack.core.streamers.single.SingleStreamer
import io.github.thibaultbee.streampack.core.streamers.single.cameraSingleStreamer
import io.github.thibaultbee.streampack.ext.rtmp.elements.endpoints.RtmpEndpointFactory

class InstanceManager(var context: Context? = null) {
    private var instance: SingleStreamer? = null

    suspend fun getInstance(): SingleStreamer {
        if (instance == null) {
            instance = cameraSingleStreamer(
                context = context!!,
                endpointFactory = RtmpEndpointFactory()
            )
        }
        return instance!!
    }

    fun dispose() {
        instance = null
    }
}
