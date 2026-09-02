# Changelog

All notable changes to this project will be documented in this file.

## [1.0.3] - 2026-09-02

### Changed

- Shortened the pubspec description to fit the 60-180 character limit so the
  package passes the "Provide a valid pubspec.yaml" pub.dev scoring check.

## [1.0.2] - 2026-09-02

### Fixed

- Android: camera preview froze (stuck on the last frame) after calling
  `toggleCamera()` / `setCameraId()`. With StreamPack 3.2.0, `setCameraId`
  swaps the video source and releases the previous camera source without
  transferring the preview to the new one. The manager now stops the preview
  before switching cameras and restarts it on the new camera afterwards
  (mirroring the existing `setVideoConfig` behavior), and restores the
  preview if the switch fails.

## [1.0.1] - 2026-09-02

- Docs: fresh changelog for the new package; README badges and links now
  point to the maintainer ([@realmastana](https://twitter.com/realmastana))
  while crediting api.video for the original work
- CI: GitHub Actions workflows for analysis/tests and automatic publishing
  to pub.dev whenever the version in `pubspec.yaml` changes

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
