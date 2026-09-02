import AVFoundation
import Flutter

class LiveStreamHostApiImpl: LiveStreamHostApi {
    private let instanceManager: InstanceManager
    private let textureRegistry: FlutterTextureRegistry
    private let liveStreamFlutterApi: LiveStreamFlutterApi

    private var flutterView: LiveStreamViewManager?

    init(instanceManager: InstanceManager, textureRegistry: FlutterTextureRegistry, liveStreamFlutterApi: LiveStreamFlutterApi) {
        self.instanceManager = instanceManager
        self.textureRegistry = textureRegistry
        self.liveStreamFlutterApi = liveStreamFlutterApi
    }

    func create() throws -> Int64 {
        try MainActor.assumeIsolated {
            flutterView = try instanceManager.create(textureRegistry: textureRegistry)
            flutterView!.delegate = self
            return flutterView!.textureId
        }
    }

    func dispose() throws {
        MainActor.assumeIsolated {
            flutterView?.dispose()
            flutterView = nil
        }
    }

    func setVideoConfig(videoConfig: NativeVideoConfig, completion: @escaping (Result<Void, any Error>) -> Void) {
        MainActor.assumeIsolated {
            flutterView!.videoConfig = videoConfig.toVideoConfig()
            completion(.success(()))
        }
    }

    func setAudioConfig(audioConfig: NativeAudioConfig, completion: @escaping (Result<Void, any Error>) -> Void) {
        MainActor.assumeIsolated {
            flutterView!.audioConfig = audioConfig.toAudioConfig()
            completion(.success(()))
        }
    }

    func startStreaming(streamKey: String, url: String) throws {
        try MainActor.assumeIsolated {
            try flutterView!.startStreaming(streamKey: streamKey, url: url)
        }
    }

    func stopStreaming() throws {
        MainActor.assumeIsolated {
            flutterView!.stopStreaming()
        }
    }

    func startPreview(completion: @escaping (Result<Void, any Error>) -> Void) {
        MainActor.assumeIsolated {
            flutterView!.startPreview()
            completion(.success(()))
        }
    }

    func stopPreview() throws {
        MainActor.assumeIsolated {
            flutterView!.stopPreview()
        }
    }

    func getIsStreaming() throws -> Bool {
        MainActor.assumeIsolated {
            flutterView?.isStreaming ?? false
        }
    }

    func getCameraId() throws -> String {
        MainActor.assumeIsolated {
            flutterView!.camera!.uniqueID
        }
    }

    func setCameraId(cameraId: String, completion: @escaping (Result<Void, Error>) -> Void) {
        MainActor.assumeIsolated {
            flutterView!.camera = DeviceProvider.getCamera(uniqueID: cameraId)
            completion(.success(()))
        }
    }

    func getIsMuted() throws -> Bool {
        MainActor.assumeIsolated {
            flutterView?.isMuted ?? false
        }
    }

    func setIsMuted(isMuted: Bool) throws {
        MainActor.assumeIsolated {
            flutterView!.isMuted = isMuted
        }
    }

    func getVideoResolution() throws -> NativeResolution? {
        MainActor.assumeIsolated {
            flutterView?.videoConfig.resolution.toNativeResolution()
        }
    }
}

extension LiveStreamHostApiImpl: LiveStreamViewManagerDelegate {
    func connectionSuccess() {
        liveStreamFlutterApi.onIsConnectedChanged(isConnected: true, completion: { _ in })
    }

    func disconnection() {
        liveStreamFlutterApi.onIsConnectedChanged(isConnected: false, completion: { _ in })
    }

    func connectionFailed(_ message: String) {
        liveStreamFlutterApi.onConnectionFailed(message: message, completion: { _ in })
    }

    func error(_ error: any Error) {
        liveStreamFlutterApi.onError(code: "native-error", message: error.localizedDescription, completion: { _ in })
    }

    func videoSizeChanged(_ size: CGSize) {
        liveStreamFlutterApi.onVideoSizeChanged(resolution: size.toNativeResolution(), completion: { _ in })
    }
}
