<!--<documentation_excluded>-->
[![badge](https://img.shields.io/twitter/follow/api_video?style=social)](https://twitter.com/intent/follow?screen_name=api_video)
&nbsp; [![badge](https://img.shields.io/github/stars/apivideo/api.video-flutter-live-stream?style=social)](https://github.com/apivideo/api.video-flutter-live-stream)
&nbsp; [![badge](https://img.shields.io/discourse/topics?server=https%3A%2F%2Fcommunity.api.video)](https://community.api.video)
![](https://github.com/apivideo/.github/blob/main/assets/apivideo_banner.png)

<h1 align="center">Flutter RTMP live stream client</h1>

[api.video](https://api.video) is the video infrastructure for product builders. Lightning fast
video APIs for integrating, scaling, and managing on-demand & low latency live streaming features in
your app.

## Table of contents

- [Table of contents](#table-of-contents)
- [Project description](#project-description)
- [Getting started](#getting-started)
    - [Installation](#installation)
    - [Permissions](#permissions)
    - [Code sample](#code-sample)
        - [Manage application lifecycle](#manage-application-lifecycle)
- [Example App](#example-app)
    - [Setup](#setup)
        - [Android](#android)
        - [iOS](#ios)
- [Plugins](#plugins)
- [FAQ](#faq)

<!--</documentation_excluded>-->
<!--<documentation_only>
---
title: Flutter RTMP live stream client
meta:
description: The official Flutter RTMP live stream client for
api.video. [api.video](https://api.video/) is the video infrastructure for product builders.
Lightning fast video APIs for integrating, scaling, and managing on-demand & low latency live
streaming features in your app.
---

# Flutter RTMP Live stream Client

[api.video](https://api.video/) is the video infrastructure for product builders. Lightning fast
video APIs for integrating, scaling, and managing on-demand & low latency live streaming features in
your app.

</documentation_only>-->

## Project description

This module is made for broadcasting RTMP live stream from smartphone camera.

## Getting started

### Installation

Run the following command at the root of your project:

```shell
flutter pub add apivideo_live_stream
```

In your dart file, import the package:

```dart 
import 'package:apivideo_live_stream/apivideo_live_stream.dart';
```

### Permissions

To be able to broadcast, you must:

1. On Android: ask for internet, camera and microphone permissions:

```xml

<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
</manifest>
```

The library will require android.permission.CAMERA and android.permission.RECORD_AUDIO at runtime.
You don't need to request them.

2. On iOS: update the Info.plist with a usage description for camera and microphone

```xml

<key>NSCameraUsageDescription</key>
<string>Your own description of the purpose</string>
<key>NSMicrophoneUsageDescription</key>
<string>Your own description of the purpose</string>
```

### Code sample

1. Creates a live stream controller

```dart

final ApiVideoLiveStreamController _controller = ApiVideoLiveStreamController(
    initialAudioConfig: AudioConfig(), initialVideoConfig: VideoConfig.withDefaultBitrate());
```

2. Initializes the live stream controller

```dart
await _controller.initialize();
```

3. Adds a CameraPreview widget as a child of your view

```dart
@override
Widget build(BuildContext context) {
  return SizedBox(
      width: 300.0,
      height: 300.0,
      child: ApiVideoCameraPreview(controller: _controller));
}
```

`ApiVideoCameraPreview` parameters:

- `controller`: the live stream controller
- `fit`: the fit of the preview (default is BoxFit.contain,
  see [BoxFit](https://api.flutter.dev/flutter/painting/BoxFit.html) for more information)
- `child`: a child widget to overlay on top of the preview (optional)

4. Starts a live stream

```dart
_controller.startStreaming("YOUR_STREAM_KEY");
```

5. Stops streaming and preview

```dart
_controller.stop();
```

#### Manage application lifecycle

On the application side, you must manage application lifecycle:

```dart
@override
void didChangeAppLifecycleState(AppLifecycleState state) {
  if (state == AppLifecycleState.inactive) {
    _controller.stop();
  } else if (state == AppLifecycleState.resumed) {
    _controller.startPreview();
  }
}
```

## Example App

You can try
our [example app](https://github.com/apivideo/api.video-flutter-live-stream/tree/master/example),
feel free to test it.

### Setup

Be sure to follow the [Flutter installation steps](https://docs.flutter.dev/get-started/) before
anything.

1) Open Android Studio
2) File > New > Project from Version Control

In URL field, type:

```shell
git@github.com:apivideo/api.video-flutter-live-stream.git
```

Wait for the indexation to finish.

#### Android

Connect an Android device to your computer and click on the `Run main.dart` button.

#### iOS

1) Connect an iOS device to your computer and click on the `Run main.dart` button.

2) The build will fail because you haven't set your development profile, sign your application:

Open Xcode, click on "Open a project or file" and open
the `YOUR_PROJECT_NAME/example/ios/Runner.xcworkspace` file.
<br />Click on Example, go in `Signin & Capabilities` tab, add your team and create a unique bundle
identifier.

### iOS: Xcode 26 / Swift 6.3 compiler crash workaround

With Xcode 26 (Swift 6.3+), release builds can crash inside the Swift compiler while compiling
HaishinKit 1.9.3 (pulled in by the `ApiVideoLiveStream` pod):

```
Found ownership error?!
While running pass ... SILFunctionTransform "CopyPropagation" on SILFunction
"...MixerNodeC6format..." (at Pods/HaishinKit/Sources/IO/AudioNode.swift:137:5)
```

This is a Swift compiler bug triggered by old HaishinKit code, not a source error. Two
workarounds exist (pick one):

#### Option 1: build HaishinKit without optimizations (no external dependency)

Add this `post_install` hook to your app's `ios/Podfile` to build HaishinKit at `-Onone`:

```ruby
post_install do |installer|
  installer.pods_project.targets.each do |target|
    flutter_additional_ios_build_settings(target)

    if target.name == 'HaishinKit'
      target.build_configurations.each do |config|
        config.build_settings['SWIFT_OPTIMIZATION_LEVEL'] = '-Onone'
      end
    end
  end
end
```

Then run `flutter clean` (or delete `ios/Pods` and `ios/Podfile.lock`) and rebuild. The example
app's `ios/Podfile` already includes this hook. Impact is negligible (HaishinKit only does
RTMP/muxing glue; encoding is hardware-accelerated).

#### Option 2: patch HaishinKit so it compiles optimized (keeps `-O`)

The crash is in the optimizer on a single function (`MixerNode.init(format:)`), and it can be
dodged by marking that function `@_optimize(none)`. A ready-made patch is checked into this
repository: `patches/haishinkit-1.9.3-xcode26.patch`.

1. Fork [HaishinKit.swift](https://github.com/shogo4405/HaishinKit.swift) at tag `1.9.3`.
2. Apply the patch: `git apply patches/haishinkit-1.9.3-xcode26.patch` (or add
   `@_optimize(none)` above `init(format: AVAudioFormat)` in `Sources/IO/AudioNode.swift`).
3. Commit and tag your fork (e.g. `1.9.3-xcode26`). Keep the podspec version at `1.9.3`.
4. In your app's `ios/Podfile`, inside `target 'Runner' do`, add:
   ```ruby
   pod 'HaishinKit', :git => 'https://github.com/<your-github-username>/HaishinKit.swift.git', :tag => '1.9.3-xcode26'
   ```
   (You can then remove the Option 1 hook.)
5. Delete `ios/Pods` and `ios/Podfile.lock`, then rebuild.

The rest of HaishinKit stays fully optimized. Note: `@_optimize(none)` is an underscored
internal Swift attribute (stable for years, used inside Apple's own frameworks) — if a future
Xcode release changes the optimizer again, re-check the patch.

## Plugins

api.video Flutter live stream library is using external native libraries:

| Plugin     | README       |
|------------|--------------|
| StreamPack | [StreamPack] |
| HaishinKit | [HaishinKit] |

## FAQ

If you have any questions, ask us in the [community](https://community.api.video) or
use [issues](https://github.com/apivideo/api.video-flutter-live-stream/issues).

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

[StreamPack]: <https://github.com/ThibaultBee/StreamPack>

[HaishinKit]: <https://github.com/shogo4405/HaishinKit.swift>

