import AVFoundation
import Flutter
import Foundation
import HaishinKit

final class PreviewTexture: NSObject, FlutterTexture, MediaMixerOutput, @unchecked Sendable {
    private var currentSampleBuffer: CMSampleBuffer?
    private let bufferLock = NSLock()
    private let registry: FlutterTextureRegistry
    private(set) var textureId: Int64 = 0

    public init(registry: FlutterTextureRegistry) {
        self.registry = registry
        super.init()
        textureId = registry.register(self)
    }

    func copyPixelBuffer() -> Unmanaged<CVPixelBuffer>? {
        bufferLock.lock()
        defer { bufferLock.unlock() }
        guard let currentSampleBuffer = currentSampleBuffer,
              let imageBuffer = CMSampleBufferGetImageBuffer(currentSampleBuffer)
        else {
            return nil
        }

        return Unmanaged<CVPixelBuffer>.passRetained(imageBuffer)
    }

    func dispose() {
        registry.unregisterTexture(textureId)
    }

    // MARK: - MediaMixerOutput

    var videoTrackId: UInt8? {
        get async { nil }
    }

    var audioTrackId: UInt8? {
        get async { nil }
    }

    func mixer(_ mixer: MediaMixer, didOutput sampleBuffer: CMSampleBuffer) {
        bufferLock.lock()
        currentSampleBuffer = sampleBuffer
        bufferLock.unlock()
        registry.textureFrameAvailable(textureId)
    }

    func mixer(_ mixer: MediaMixer, didOutput buffer: AVAudioPCMBuffer, when: AVAudioTime) {
        // Audio is not rendered through the Flutter texture.
    }

    func selectTrack(_ id: UInt8?, mediaType: CMFormatDescription.MediaType) async {
        // Track selection is not used.
    }
}
