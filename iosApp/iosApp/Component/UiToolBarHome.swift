//
//  UiToolBarHome.swift
//  iosApp
//

import SwiftUI

struct UiToolBarHome: View {
    var typeFlow: Bool
    var onClick: () -> Void = {}
    var onOpenDrawer: () -> Void = {}
    @ObservedObject var cartManager = CartManager.shared
    
    var welcomeText: String {
        let names = cartManager.userName.trimmingCharacters(in: .whitespacesAndNewlines)
        let components = names.components(separatedBy: .whitespacesAndNewlines).filter { !$0.isEmpty }
        if let firstName = components.first, !firstName.isEmpty {
            return "¡Hola, \(firstName)!"
        } else {
            return "¡Hola!"
        }
    }

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "line.3.horizontal")
                .foregroundColor(Color.uiTayRed600)
                .font(.system(size: 22, weight: .bold))
                .onTapGesture {
                    onOpenDrawer()
                }
            
            Text(welcomeText)
                .font(Font.uiMontS18)
                .foregroundColor(Color.uiTayRed600)
                .lineLimit(1)
                .minimumScaleFactor(0.75)
                .onTapGesture {
                    onOpenDrawer()
                }

            Spacer(minLength: 4)
                
            if typeFlow {
                Image(uiName: "ic_logo_pizzzeria")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 110, height: 36)
            } else {
                Image(systemName: "arrow.clockwise")
                    .foregroundColor(Color.uiTayRed600)
                    .font(.system(size: 20, weight: .bold))
                    .onTapGesture {
                        onClick()
                    }
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
        .onAppear {
            cartManager.loadUserAddress()
        }
    }
}
