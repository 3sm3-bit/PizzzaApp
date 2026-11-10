import SwiftUI

public struct PizzaColors {
    public static let red600 = Color(hex: 0xA62626)
    public static let red400 = Color(hex: 0xF87171)
    public static let red50 = Color(hex: 0xFEF2F2)
    public static let green600 = Color(hex: 0x16A34A)
    public static let grey800 = Color(hex: 0x1F2937)
    public static let background = Color(hex: 0xF0F2F5)
    
    public init() {}
}

public struct PizzaFonts {
    public static let bold35 = Font.system(size: 35, weight: .bold)
    public static let bold20 = Font.system(size: 20, weight: .bold)
    public static let bold16 = Font.system(size: 16, weight: .bold)
    public static let bold14 = Font.system(size: 14, weight: .bold)
    public static let bold12 = Font.system(size: 12, weight: .bold)
    
    public static let medium14 = Font.system(size: 14, weight: .medium)
    public static let medium12 = Font.system(size: 12, weight: .medium)
    public static let medium10 = Font.system(size: 10, weight: .medium)
    
    public init() {}
}

public extension Color {
    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 8) & 0xff) / 255,
            blue: Double(hex & 0xff) / 255,
            opacity: alpha
        )
    }
}
