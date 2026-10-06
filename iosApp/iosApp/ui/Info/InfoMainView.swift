//
//  InfoMainView.swift
//  iosApp
//

import SwiftUI
import TaySwitfUILibrary

struct InfoMainView: View {
    @Environment(\.dismiss) var dismiss
    
    @State private var selectedDetailTitle: String? = nil
    @State private var selectedDetailContent: String? = nil
    @State private var showDetail: Bool = false
    
    @State private var selectedWebTitle: String? = nil
    @State private var selectedWebUrl: String? = nil
    @State private var showWeb: Bool = false

    var body: some View {
        VStack(spacing: 0) {
            UiTayCToolBar(uiTayText: "Información y Ayuda") { _ in
                dismiss()
            }
            
            ScrollView {
                VStack(spacing: 24) {
                    HStack(spacing: 40) {
                        ContactButton(iconName: "ic_social_w") {
                            openWhatsApp()
                        }
                        ContactButton( iconName: "ic_email_contact") {
                            openEmail()
                        }
                        ContactButton(iconName: "ic_call_contact") {
                            openCall()
                        }
                    }
                    .padding(.top, 16)

                    Spacer().frame(height: 8)

                    Text("Información y Recursos")
                        .font(Font.uiMontB18)
                        .foregroundColor(Color.uiTayRed600)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    VStack(spacing: 0) {
                        ResourceRow(
                            title: "¿Cómo usar la app?",
                            subtitle: "Guías y tutoriales de pedidos"
                        ) {
                            selectedDetailTitle = "¿Cómo usar la app?"
                            selectedDetailContent = InfoStrings.guideHowToUseApp
                            showDetail = true
                        }
                        
                        Divider().background(Color.uiTayRed50)
                        
                        ResourceRow(
                            title: "No recuerdo mis datos",
                            subtitle: "Recuperación de acceso y cuenta"
                        ) {
                            selectedDetailTitle = "No recuerdo mis datos"
                            selectedDetailContent = InfoStrings.guideForgotData
                            showDetail = true
                        }
                        
                        Divider().background(Color.uiTayRed50)
                        
                        ResourceRow(
                            title: "Sobre Nosotros",
                            subtitle: "Conoce más de nuestra pizzería"
                        ) {
                            selectedWebTitle = "Sobre Nosotros"
                            selectedWebUrl = "https://lapizzzeria.com/conoce-mas-sobre-la-pizzzeria/"
                            showWeb = true
                        }
                        
                        Divider().background(Color.uiTayRed50)
                        
                        ResourceRow(
                            title: "Términos y Condiciones",
                            subtitle: "Conoce las reglas de uso de la app"
                        ) {
                            selectedDetailTitle = "Términos y Condiciones"
                            selectedDetailContent = InfoStrings.guideTermsAndConditions
                            showDetail = true
                        }
                        
                        Divider().background(Color.uiTayRed50)
                        
                        ResourceRow(
                            title: "Política de Privacidad y Seguridad",
                            subtitle: "Protección de tus datos personales"
                        ) {
                            selectedWebTitle = "Política de Privacidad"
                            selectedWebUrl = "https://lapizzzeria.com/aviso-de-privacidad/"
                            showWeb = true
                        }
                    }
                    .background(Color.white)
                    .cornerRadius(16)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.uiTayRed50, lineWidth: 3)
                    )
                    .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                }
                .padding(24)
            }
        }
        .uiTayHideToolbar()
        .background(Color.white)
        .uiTayNavigate(
            item: selectedDetailTitle,
            to: { title in
                InfoDetailView(
                    title: title,
                    htmlContent: selectedDetailContent ?? ""
                )
            },
            when: $showDetail
        )
        .uiTayNavigate(
            item: selectedWebTitle,
            to: { title in
                InfoWebView(
                    title: title,
                    urlString: selectedWebUrl ?? ""
                )
            },
            when: $showWeb
        )
    }

    private func openWhatsApp() {
        let phone = "4492057452"
        let msg = "¡Hola me gustaria consultar:".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        if let url = URL(string: "https://wa.me/52\(phone)?text=\(msg)") {
            UIApplication.shared.open(url)
        }
    }

    private func openEmail() {
        let email = "lapizzzeria@outlook.com"
        let subject = "Hola me gustaría consultar:".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        let body = "Hola, me gustaría realizar la siguiente consulta:".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        if let url = URL(string: "mailto:\(email)?subject=\(subject)&body=\(body)") {
            UIApplication.shared.open(url)
        }
    }

    private func openCall() {
        if let url = URL(string: "tel://4494487490") {
            UIApplication.shared.open(url)
        }
    }
}

private struct ContactButton: View {
    let iconName: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(iconName)
                .resizable()
                .scaledToFit()
        }
        .buttonStyle(.plain)
        .frame(width: 64, height: 64)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .shadow(color: Color.black.opacity(0.12), radius: 6, x: 0, y: 4)
    }
}

private struct ResourceRow: View {
    let title: String
    let subtitle: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(Font.uiMontB14)
                        .foregroundColor(.black)
                    Text(subtitle)
                        .font(Font.uiMontM12)
                        .foregroundColor(.gray)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(Color.uiTayRed600)
                    .font(.system(size: 14, weight: .bold))
            }
            .padding(16)
        }
    }
}
