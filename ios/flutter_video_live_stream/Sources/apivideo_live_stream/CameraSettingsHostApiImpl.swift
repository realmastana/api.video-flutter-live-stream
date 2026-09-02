import AVFoundation

class CameraSettingsHostApiImpl: CameraSettingsHostApi {
    let instanceManager: InstanceManager

    init(instanceManager: InstanceManager) {
        self.instanceManager = instanceManager
    }

    func setZoomRatio(zoomRatio: Double) throws {
        MainActor.assumeIsolated {
            guard let liveStream = instanceManager.liveStream else {
                return
            }
            liveStream.zoomRatio = CGFloat(zoomRatio)
        }
    }

    func getZoomRatio() throws -> Double {
        MainActor.assumeIsolated {
            guard let liveStream = instanceManager.liveStream else {
                return 1.0
            }
            return Double(liveStream.zoomRatio)
        }
    }
}
