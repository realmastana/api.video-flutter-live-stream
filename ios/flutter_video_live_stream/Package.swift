// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "flutter_video_live_stream",
    platforms: [
        .iOS(.v15)
    ],
    products: [
        .library(name: "flutter-video-live-stream", targets: ["flutter_video_live_stream"])
    ],
    dependencies: [
        .package(name: "FlutterFramework", path: "../FlutterFramework"),
        // Fork of api.video-swift-live-stream migrated to HaishinKit 2.2.5 and
        // distributed as a Swift package (the upstream SDK is CocoaPods-only
        // and still pins the Xcode 26-incompatible HaishinKit 1.9.3).
        .package(url: "https://github.com/realmastana/api.video-swift-live-stream.git", branch: "main"),
        .package(url: "https://github.com/HaishinKit/HaishinKit.swift", exact: "2.2.5")
    ],
    targets: [
        .target(
            name: "flutter_video_live_stream",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework"),
                .product(name: "ApiVideoLiveStream", package: "api.video-swift-live-stream"),
                .product(name: "HaishinKit", package: "HaishinKit.swift")
            ]
        )
    ]
)
