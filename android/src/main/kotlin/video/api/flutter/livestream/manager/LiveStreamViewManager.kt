package video.api.flutter.livestream.manager

import android.Manifest
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.util.Size
import android.view.Surface
import io.flutter.view.TextureRegistry
import io.github.thibaultbee.streampack.core.configuration.mediadescriptor.UriMediaDescriptor
import io.github.thibaultbee.streampack.core.elements.sources.audio.audiorecord.IAudioRecordSource
import io.github.thibaultbee.streampack.core.elements.sources.video.camera.ICameraSource
import io.github.thibaultbee.streampack.core.interfaces.setCameraId
import io.github.thibaultbee.streampack.core.interfaces.startPreview
import io.github.thibaultbee.streampack.core.interfaces.stopPreview
import io.github.thibaultbee.streampack.core.streamers.single.AudioConfig
import io.github.thibaultbee.streampack.core.streamers.single.SingleStreamer
import io.github.thibaultbee.streampack.core.streamers.single.VideoConfig
import io.github.thibaultbee.streampack.core.utils.extensions.isClosedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LiveStreamViewManager(
    private val streamer: SingleStreamer,
    textureRegistry: TextureRegistry,
    private val permissionsManager: PermissionsManager,
    private val onConnectionSucceeded: () -> Unit,
    private val onDisconnected: () -> Unit,
    private val onConnectionFailed: (String) -> Unit,
    private val onGenericError: (Exception) -> Unit,
    private val onVideoSizeChanged: (Size) -> Unit,
) {
    private val flutterTexture = textureRegistry.createSurfaceTexture()
    val textureId: Long
        get() = flutterTexture.id()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var _isPreviewing = false
    private var _isStreaming = false
    val isStreaming: Boolean
        get() = _isStreaming

    private var _videoConfig: VideoConfig? = null
    val videoConfig: VideoConfig
        get() = _videoConfig!!

    fun setVideoConfig(
        videoConfig: VideoConfig,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (isStreaming) {
            throw UnsupportedOperationException("You have to stop streaming first")
        }

        onVideoSizeChanged(videoConfig.resolution)

        val wasPreviewing = _isPreviewing
        if (wasPreviewing) {
            stopPreview()
        }
        try {
            runBlocking { streamer.setVideoConfig(videoConfig) }
            _videoConfig = videoConfig
            if (wasPreviewing) {
                startPreview(onSuccess, onError)
            } else {
                onSuccess()
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private var _audioConfig: AudioConfig? = null
    val audioConfig: AudioConfig
        get() = _audioConfig!!

    fun setAudioConfig(
        audioConfig: AudioConfig,
        enableEchoCanceler: Boolean,
        enableNoiseSuppressor: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (isStreaming) {
            throw UnsupportedOperationException("You have to stop streaming first")
        }

        permissionsManager.requestPermission(
            Manifest.permission.RECORD_AUDIO,
            onGranted = {
                try {
                    runBlocking {
                        streamer.setAudioConfig(audioConfig)
                        applyAudioEffects(enableEchoCanceler, enableNoiseSuppressor)
                    }
                    _audioConfig = audioConfig
                    onSuccess()
                } catch (e: Exception) {
                    onError(e)
                }
            },
            onShowPermissionRationale = { _ ->
                onError(SecurityException("Missing permission Manifest.permission.RECORD_AUDIO"))
            },
            onDenied = {
                onError(SecurityException("Missing permission Manifest.permission.RECORD_AUDIO"))
            })
    }

    private suspend fun applyAudioEffects(
        enableEchoCanceler: Boolean,
        enableNoiseSuppressor: Boolean
    ) {
        val audioSource =
            streamer.audioInput.sourceFlow.value as? IAudioRecordSource ?: return
        if (enableEchoCanceler) {
            audioSource.addEffect(AudioEffect.EFFECT_TYPE_AEC)
        }
        if (enableNoiseSuppressor) {
            audioSource.addEffect(AudioEffect.EFFECT_TYPE_NS)
        }
    }

    var isMuted: Boolean
        get() = streamer.audioInput.isMuted
        set(value) {
            streamer.audioInput.isMuted = value
        }

    val camera: String
        get() = (streamer.videoInput.sourceFlow.value as? ICameraSource)?.cameraId ?: ""

    fun setCamera(camera: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        permissionsManager.requestPermission(
            Manifest.permission.CAMERA,
            onGranted = {
                try {
                    runBlocking { streamer.setCameraId(camera) }
                    onSuccess()
                } catch (e: Exception) {
                    onError(e)
                }
            },
            onShowPermissionRationale = { _ ->
                onError(SecurityException("Missing permission Manifest.permission.CAMERA"))
            },
            onDenied = {
                onError(SecurityException("Missing permission Manifest.permission.CAMERA"))
            })
    }

    init {
        scope.launch {
            streamer.isOpenFlow.collect { isOpen ->
                if (isOpen) {
                    onConnectionSucceeded()
                }
            }
        }
        scope.launch {
            streamer.throwableFlow.collect { throwable ->
                if (throwable != null) {
                    if (throwable.isClosedException) {
                        onDisconnected()
                    } else {
                        onGenericError(
                            throwable as? Exception ?: RuntimeException(throwable)
                        )
                    }
                }
            }
        }
    }

    fun dispose() {
        scope.cancel()
        runBlocking {
            streamer.stopStream()
            streamer.close()
            streamer.stopPreview()
            streamer.release()
        }
        flutterTexture.release()
    }

    fun startStream(url: String) {
        runBlocking {
            try {
                streamer.open(UriMediaDescriptor(Uri.parse(url)))
            } catch (e: Exception) {
                onConnectionFailed(e.message ?: "Failed to connect to $url")
                throw e
            }
            try {
                streamer.startStream()
                _isStreaming = true
            } catch (e: Exception) {
                streamer.close()
                onDisconnected()
                throw e
            }
        }
    }

    fun stopStream() {
        val isConnected = streamer.isOpenFlow.value
        runBlocking {
            streamer.stopStream()
            streamer.close()
            if (isConnected) {
                onDisconnected()
            }
            _isStreaming = false
        }
    }

    fun startPreview(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        permissionsManager.requestPermission(
            Manifest.permission.CAMERA,
            onGranted = {
                if (_videoConfig == null) {
                    onError(IllegalStateException("Video has not been configured!"))
                } else {
                    try {
                        runBlocking {
                            streamer.startPreview(getSurface(videoConfig.resolution))
                        }
                        _isPreviewing = true
                        onSuccess()
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            },
            onShowPermissionRationale = { _ ->
                onError(SecurityException("Missing permission Manifest.permission.CAMERA"))
            },
            onDenied = {
                onError(SecurityException("Missing permission Manifest.permission.CAMERA"))
            })
    }

    fun stopPreview() {
        runBlocking { streamer.stopPreview() }
        _isPreviewing = false
    }

    private fun getSurface(resolution: Size): Surface {
        val surfaceTexture = flutterTexture.surfaceTexture().apply {
            setDefaultBufferSize(
                resolution.width,
                resolution.height
            )
        }
        return Surface(surfaceTexture)
    }
}
