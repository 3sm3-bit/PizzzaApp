//
//  PizzaFont.swift
//  iosApp
//
//  Created by Developer on 7/10/26.
//

import SwiftUI

public extension Font {
    
    private enum CustomFont: String {
        case allerBold = "Aller-BoldItalic"
        case mouseDeco = "MouseDeco"
        case skiaRegular = "Skia"
    }
    
    static func aller(_ size: CGFloat) -> Font {
        .custom(CustomFont.allerBold.rawValue, size: size)
    }
    
    static func mouse(_ size: CGFloat) -> Font {
        .custom(CustomFont.mouseDeco.rawValue, size: size)
    }
    
    static func skia(_ size: CGFloat) -> Font {
        .custom(CustomFont.skiaRegular.rawValue, size: size)
    }
}
