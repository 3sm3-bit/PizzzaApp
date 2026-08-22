import Foundation
import AVFoundation

class AudioQueueManager: NSObject {
    static let shared = AudioQueueManager()
    
    private var audioPlayer: AVAudioPlayer?
    private var audioQueue: [URL] = []
    private var isPlaying = false
    
    func enqueueAudio(resourceName: String, extensionName: String) {
        guard let url = Bundle.main.url(forResource: resourceName, withExtension: extensionName) else {
            print("⚠️ AudioQueueManager: No se encontró el archivo \(resourceName).\(extensionName)")
            return
        }
        
        audioQueue.append(url)
        playNextIfPossible()
    }
    
    private func playNextIfPossible() {
        guard !isPlaying, !audioQueue.isEmpty else { return }
        
        let url = audioQueue.removeFirst()
        do {
            isPlaying = true
            audioPlayer = try AVAudioPlayer(contentsOf: url)
            audioPlayer?.delegate = self
            audioPlayer?.prepareToPlay()
            audioPlayer?.play()
            print("🍕 AudioQueueManager: Reproduciendo audio...")
        } catch {
            print("⚠️ AudioQueueManager: Error al reproducir -> \(error.localizedDescription)")
            isPlaying = false
            playNextIfPossible()
        }
    }
}

extension AudioQueueManager: AVAudioPlayerDelegate {
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        isPlaying = false
        print("🍕 AudioQueueManager: Audio terminado.")
        playNextIfPossible()
    }
}
