# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-09-01

Initial release of `flutter_video_live_stream`, a maintained fork of
[`apivideo_live_stream`](https://pub.dev/packages/apivideo_live_stream).
The upstream package is no longer actively maintained; the original work is
credited to api.video.

### Android

- Migrated to StreamPack 3.2.0 (package renamed to
  `io.github.thibaultbee.streampack.core`, streamers rebuilt around
  `SingleStreamer`, `CameraSourceFactory`, `RtmpEndpointFactory`)
- Migrated to AGP 9 built-in Kotlin (no more `kotlin-android` Gradle plugin)
  while keeping compatibility with AGP < 9
- Updated the toolchain (Gradle 9.5, AGP 9.1.0, KGP 2.4.0) so apps keep
  16 KB page-size aligned native libraries (required by Google Play for apps
  targeting Android 15+)

### iOS

- Migrated to Swift Package Manager (no more CocoaPods)
- Switched to a fork of the api.video iOS SDK that is SPM-based and uses
  HaishinKit 2.2.5, fixing the Swift 6.3 (Xcode 26) compiler crash that
  affected HaishinKit 1.9.3 — no Podfile workaround needed anymore
- Adapted to the new `@MainActor` SDK API (preview via HaishinKit 2.x
  `MediaMixerOutput`, `ApiVideoLiveStream(preview:...)` initializer)
