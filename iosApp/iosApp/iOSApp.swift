import SwiftUI

import TaySwitfUILibrary

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject var managerApp = PizzaManagerAPP()
    
    init(){
         IniTaySwitUI.initCMDefault(name: "CMDeviceHelper.cmSmallDevice")
        registerDependencies()
       }

    var body: some Scene {
        WindowGroup {
            UiTayPrivacyGuard{
                AppContentView().environmentObject(managerApp).preferredColorScheme(.light)
            }
        }
    }
}
