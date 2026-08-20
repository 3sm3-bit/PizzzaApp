import UIKit
import UserNotifications
import Shared

class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        // Inicializar Koin desde el módulo compartido
        KoinInitKt.doInitKoin()
        
        // Configurar notificaciones locales
        UNUserNotificationCenter.current().delegate = self
        
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )
        
        application.registerForRemoteNotifications()
        
        return true
    }
}

// MARK: - UNUserNotificationCenterDelegate
extension AppDelegate: UNUserNotificationCenterDelegate {
    
    // Recibir notificación cuando la app está en primer plano
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        let userInfo = notification.request.content.userInfo
        print("Notificación recibida en primer plano: \(userInfo)")
        
        // Extraer datos para la voz
        let receiver = userInfo["receiver"] as? String ?? "delivery"
        let client = userInfo["client"] as? String ?? "Tayler"
        
        let voiceMessage = "Llegó un pedido de \(receiver.uppercased()) para \(client)"
        
        // Hablar el mensaje
        VoiceManager.shared.speak(text: voiceMessage)
        
        // Mostrar el banner visualmente
        completionHandler([[.banner, .list, .sound]])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        
        let userInfo = response.notification.request.content.userInfo
        print("Usuario tocó la notificación: \(userInfo)")
        
        completionHandler()
    }
}
