import UIKit
import UserNotifications
import Shared

class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        KoinInitKt.doInitKoin()
        return true
    }
    
}
