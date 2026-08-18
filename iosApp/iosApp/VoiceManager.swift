import Foundation
import AVFoundation

class VoiceManager: NSObject, AVSpeechSynthesizerDelegate {
    static let shared = VoiceManager()
    
    private let synthesizer = AVSpeechSynthesizer()
    private var isTtsReady = false
    
    override init() {
        super.init()
        synthesizer.delegate = self
        // En iOS el motor está listo de inmediato
        isTtsReady = true
    }
    
    func speak(text: String) {
        let utterance = AVSpeechUtterance(string: text)
        utterance.voice = AVSpeechSynthesisVoice(language: "es-ES")
        utterance.rate = AVSpeechUtteranceDefaultSpeechRate
        
        // Detener cualquier habla en curso
        if synthesizer.isSpeaking {
            synthesizer.stopSpeaking(at: .immediate)
        }
        
        synthesizer.speak(utterance)
        print("VoiceManager: Hablando -> \(text)")
    }
    
    // MARK: - AVSpeechSynthesizerDelegate
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didFinish utterance: AVSpeechUtterance) {
        print("VoiceManager: Terminó de hablar")
    }
}
