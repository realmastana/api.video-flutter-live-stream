# StreamPack 3.2.0 Migration

This document records how the Android implementation of `apivideo_live_stream` was migrated from
StreamPack 2.6.x to **3.2.0**.

> ⚠️ Previous versions of this document claimed the plugin was *"fully compatible with StreamPack
> 3.2.0 – no code changes required"*. That was wrong: StreamPack 3.x is a complete rewrite
> (package rename, coroutine-based API, new streamer architecture), and the plugin did not compile
> against it. This document reflects the actual migration that was performed.

## What changed in StreamPack 3.x

1. **Package rename**: `io.github.thibaultbee.streampack.*` → `io.github.thibaultbee.streampack.core.*`
   (Maven group changed to `io.github.thibaultbee.streampack`).
2. **`CameraRtmpLiveStreamer` no longer exists**. Streamers are now built from
   `SingleStreamer` + a camera source (`CameraSourceFactory` / `cameraSingleStreamer`) and an
   endpoint factory (`RtmpEndpointFactory`, `DynamicEndpointFactory`, ...).
3. **The API is suspend/Flow based**: `open(MediaDescriptor)`, `startStream()`, `stopStream()`,
   `close()`, `release()`, `setVideoConfig(VideoCodecConfig)`, `setAudioConfig(AudioCodecConfig)`
   are all `suspend` functions; connection state is exposed through `isOpenFlow`,
   `isStreamingFlow` and errors through `throwableFlow` (instead of `OnConnectionListener` /
   `OnErrorListener`).
4. **Configuration classes moved**: `VideoConfig`/`AudioConfig` are now typealiases for
   `VideoCodecConfig`/`AudioCodecConfig` in `core.elements.encoders` (with `mimeType`, `profile`,
   `level` etc.). Audio effects (echo canceller / noise suppressor) are no longer part of the
   config; they are applied on the `IAudioRecordSource` (`addEffect(...)`).
5. **Camera utilities moved** to `core.elements.sources.video.camera.extensions`
   (`Context.cameraManager`, `Context.getCameraCharacteristics`, `CameraManager.cameras`,
   `CameraCharacteristics.zoomRatioRange`, ...). Zoom is now controlled through
   `CameraSettings.zoom.setZoomRatio(...)` / `getZoomRatio()` (suspend).

## Migration map (2.x → 3.2.0)

| 2.x (old) | 3.2.0 (new) |
| --- | --- |
| `CameraRtmpLiveStreamer(context)` | `cameraSingleStreamer(context, endpointFactory = RtmpEndpointFactory())` |
| `streamer.configure(VideoConfig)` / `AudioConfig` | `streamer.setVideoConfig(...)` / `setAudioConfig(...)` (suspend) |
| `streamer.connect(url)` / `disconnect()` | `streamer.open(UriMediaDescriptor(url))` / `close()` (suspend) |
| `streamer.onConnectionListener` / `onErrorListener` | `streamer.isOpenFlow` / `throwableFlow` (StateFlow) |
| `streamer.camera` getter/setter | `(videoInput.sourceFlow.value as? ICameraSource)?.cameraId` / `streamer.setCameraId(id)` |
| `streamer.settings.audio.isMuted` | `streamer.audioInput.isMuted` |
| `streamer.startPreview(surface)` / `stopPreview()` | `streamer.startPreview(surface)` / `stopPreview()` (suspend extensions on `IWithVideoSource`) |
| `context.cameraList` | `context.cameraManager.cameras` |
| `context.getZoomRatioRange(id)` / `getScalerMaxZoom(id)` | `context.getCameraCharacteristics(id).zoomRatioRange` / `.scalerMaxZoom` |
| `settings.zoom.zoomRatio` | `settings.zoom.getZoomRatio()` / `setZoomRatio()` (suspend) |
| `AudioConfig(enableEchoCanceler=..., enableNoiseSuppressor=...)` | `AudioCodecConfig(...)` + `(audioInput.sourceFlow.value as IAudioRecordSource).addEffect(AudioEffect.EFFECT_TYPE_AEC/NS)` |

## Files changed

- `android/src/main/kotlin/video/api/flutter/livestream/manager/InstanceManager.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/manager/LiveStreamViewManager.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/LiveStreamHostApiImpl.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/CameraInfoHostApiImpl.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/CameraProviderHostApiImpl.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/CameraSettingsHostApiImpl.kt`
- `android/src/main/kotlin/video/api/flutter/livestream/utils/ConfigExtensions.kt`
- `android/build.gradle.kts` (StreamPack 3.2.0, built-in Kotlin, no KGP)

## Built-in Kotlin migration

The plugin no longer applies the Kotlin Gradle Plugin (`kotlin("android")`). The Kotlin compiler
options are configured through the `kotlin { compilerOptions { jvmTarget = JVM_17 } }` extension:

- With AGP 9 + `android.builtInKotlin=true`, AGP's built-in Kotlin compiles the plugin sources.
- With AGP < 9 (or `android.builtInKotlin=false`), the Flutter Gradle plugin auto-applies KGP, so
  the module still compiles as before.

## 16 KB page size

StreamPack ships no native libraries and this plugin ships none either. The plugin nevertheless
forces `jniLibs.useLegacyPackaging = false` so that consuming apps keep uncompressed, 16 KB-aligned
native libraries (required by Google Play for apps targeting Android 15+). The example app was also
moved to AGP 9.1 / Gradle 9.5 / Kotlin 2.4 / `android.builtInKotlin=true` so that
`flutter build apk --release` produces a 16 KB page-size compatible APK.

## References

- StreamPack: https://github.com/ThibaultBee/StreamPack (tag `3.2.0`)
- StreamPack 3.2.0 docs: https://thibaultbee.github.io/StreamPack
- Flutter built-in Kotlin migration (plugin authors):
  https://docs.flutter.dev/release/breaking-changes/migrate-to-built-in-kotlin/for-plugin-authors
