//
//  UiToolBarHome.swift
//  iosApp
//
//  Created by Developer on 20/09/26.
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
        HStack(spacing: 12) {
            Image(systemName: "line.3.horizontal")
                .foregroundColor(Color.uiTayRed600)
                .font(.system(size: 22, weight: .bold))
                .onTapGesture {
                    onOpenDrawer()
                }
                Text(welcomeText)
                    .font(Font.uiMontS20)
                    .foregroundColor(Color.uiTayRed600)
                    .lineLimit(1)
                    .fixedSize(horizontal: true, vertical: false)
                Spacer()
                
            if(typeFlow){
                Image(uiName: "ic_logo_pizzzeria")
                    .resizable()
                    .frame(width: 120, height: 40)
              
            }else{
                Image(systemName: "arrow.clockwise")
                 .foregroundColor(Color.uiTayRed600)
                 .font(.system(size: 20, weight: .bold))
                 .onTapGesture {
                     onClick()
                 }
             }
        }
        .padding(.horizontal)
        .padding(.top, 8).padding(.trailing,12)
        .onAppear {
            cartManager.loadUserAddress()
        }
    }
}

