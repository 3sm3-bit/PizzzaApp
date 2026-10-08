//
//  HomeDrawerView.swift
//  iosApp
//

import SwiftUI
import TaySwitfUILibrary

struct HomeDrawerView: View {
    @Binding var isOpen: Bool
    let userName: String
    let onNavigateToInfo: () -> Void
    var onNavigateToDeleteUser: () -> Void = {}
    let onLogout: () -> Void

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                if isOpen {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            withAnimation(.easeInOut) {
                                isOpen = false
                            }
                        }
                    
                    HStack(spacing: 0) {
                        drawerContent(width: geometry.size.width * 0.8)
                        Spacer()
                    }
                    .transition(.move(edge: .leading))
                }
            }
        }
        .animation(.easeInOut, value: isOpen)
    }

    private func drawerContent(width: CGFloat) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 12) {
            Spacer().frame(height: 56)
            HStack(spacing: 8) {
                ZStack {
                        Color.white
                        Image("ic_logo_m_pizzzeria")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 50, height: 50)
                    }
                    .frame(width: 65, height: 65)
                    .clipShape(Circle())
                    .overlay(
                        Circle()
                            .stroke(Color.uiTayRed600, lineWidth: 1)
                    )
                    
                    Text("¡Hola, \(userName.isEmpty ? "Cliente" : userName)!")
                        .font(Font.uiMontB16)
                        .foregroundColor(.black)
                        .lineLimit(1)
                    Spacer()
                }
            }
            .padding(24)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.white)
            Divider().padding(.vertical, 8)
            ScrollView {
                VStack(spacing: 8) {
                    DrawerRow(
                        title: "Información y Ayuda",
                        systemIcon: "ic_client"
                    ) {
                        withAnimation { isOpen = false }
                        onNavigateToInfo()
                    }
                    
                    DrawerRow(
                        title: "Nuestro facebook",
                        systemIcon: "ic_facebook"
                    ) {
                        withAnimation { isOpen = false }
                        openUrl("https://www.facebook.com/61571443119838")
                    }
                    
                    DrawerRow(
                        title: "Nuestro instagram",
                        systemIcon: "ic_instagram"
                    ) {
                        withAnimation { isOpen = false }
                        openUrl("https://www.instagram.com/lapizzzeria_")
                    }
                    
                    DrawerRow(
                        title: "Nuestro tiktok",
                        systemIcon: "ic_tiktok"
                    ) {
                        withAnimation { isOpen = false }
                        openUrl("https://www.tiktok.com/@lapizzzeria")
                    }
                    
                    Divider().padding(.vertical, 8)
                    Button(action: {
                        withAnimation { isOpen = false }
                        onNavigateToDeleteUser()
                    }) {
                        HStack(spacing: 16) {
                            Image("ic_delete_user")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 24, height: 24)
                        
                            Text("Eliminar usuario")
                                .font(Font.uiMontM14)
                                .foregroundColor(Color.uiTayRed600)
                            
                            Spacer()
                        }
                        .padding(.vertical, 12)
                        .padding(.horizontal, 8)
                    }

                    Button(action: {
                        withAnimation { isOpen = false }
                        onLogout()
                    }) {
                        HStack(spacing: 16) {
                            Image(systemName:"rectangle.portrait.and.arrow.right")
                                .resizable()
                                .scaledToFit()
                                .foregroundColor(Color.uiTayRed600)
                                .frame(width: 24, height: 24)
                        
                            Text("Cerrar sesión")
                                .font(Font.uiMontM14)
                                .foregroundColor(Color.uiTayRed600)
                            
                            Spacer()
                        }
                        .padding(.vertical, 12)
                        .padding(.horizontal, 8)
                    }
                }
                .padding(16)
            }
            .background(Color.white)
        }
        .frame(width: width)
        .background(Color.white)
        .edgesIgnoringSafeArea(.vertical)
    }

    private func openUrl(_ urlString: String) {
        if let url = URL(string: urlString) {
            UIApplication.shared.open(url)
        }
    }
}

private struct DrawerRow: View {
    let title: String
    let systemIcon: String
    var color: Color = Color.uiTayRed600
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                Image(systemIcon)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24, height: 24)
            
                Text(title)
                    .font(Font.uiMontM14)
                    .foregroundColor(color == .red ? .red : .black)
                
                Spacer()
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 8)
        }
    }
}
