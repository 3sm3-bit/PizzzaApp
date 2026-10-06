//
//  UtilsSecurity.swift
//  iosApp
//
//  Created by Developer on 5/10/26.
//

import Foundation
import UIKit

class UtilsSecurity{
    
    static func getJailbrokenStatus() -> Bool {
        var isSimulator: Bool {
#if targetEnvironment(simulator)
            return true
#else
            return false
#endif
        }
        switch(isSimulator){
        case true : return true
            case false : return neoValidRoot()}
    }
    
    private static func neoValidRoot()-> Bool{
        if FileManager.default.fileExists(atPath: "\(uiSlash)Applications\(uiSlash)Cydia.app")
            || FileManager.default.fileExists(atPath: "\(uiSlash)Library\(uiSlash)MobileSubstrate\(uiSlash)MobileSubstrate.dylib")
            || FileManager.default.fileExists(atPath: "\(uiSlash)bin\(uiSlash)bash")
            || FileManager.default.fileExists(atPath: "\(uiSlash)usr\(uiSlash)sbin\(uiSlash)sshd")
            || FileManager.default.fileExists(atPath: "\(uiSlash)etc\(uiSlash)apt")
            || FileManager.default.fileExists(atPath: "\(uiSlash)private\(uiSlash)var\(uiSlash)lib\(uiSlash)apt\(uiSlash)")
            || UIApplication.shared.canOpenURL(URL(string:"cydia:\(uiSlashDupla)package\(uiSlash)com.example.package")!) {
            return true
        }
        let stringToWrite = "Jailbreak Test"
        do {
            try stringToWrite.write(toFile:"\(uiSlash)private\(uiSlash)JailbreakTest.txt", atomically:true, encoding:String.Encoding.utf8)
            return true
        } catch {
            return false
        }
        
    }
    
}
