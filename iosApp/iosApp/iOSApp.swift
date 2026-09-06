import SwiftUI

import TaySwitfUILibrary

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init(){
         IniTaySwitUI.initCMDefault(name: "CMDeviceHelper.cmSmallDevice")
        
       }

    var body: some Scene {
        WindowGroup {
            SplashView()
        }
    }
}
