//
//  InfoWebView.swift
//  iosApp
//

import SwiftUI
import WebKit
import TaySwitfUILibrary

struct InfoWebView: View {
    let title: String
    let urlString: String
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = true

    var body: some View {
        VStack(spacing: 0) {
            UiTayCToolBar(uiTayText: title) { _ in
                dismiss()
            }
            
            ZStack {
                if let url = URL(string: urlString) {
                    SimpleWebView(url: url, isLoading: $isLoading)
                } else {
                    Text("URL no válida")
                        .foregroundColor(.gray)
                }

                if isLoading {
                    ProgressView()
                        .scaleEffect(1.5)
                        .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
                }
            }.background(Color.white)
        }
        .uiTayHideToolbar()
        .background(Color.white)
    }
}

private struct SimpleWebView: UIViewRepresentable {
    let url: URL
    @Binding var isLoading: Bool

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.allowsBackForwardNavigationGestures = true
        webView.navigationDelegate = context.coordinator
        let request = URLRequest(url: url)
        webView.load(request)
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, WKNavigationDelegate {
        var parent: SimpleWebView

        init(_ parent: SimpleWebView) {
            self.parent = parent
        }

        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            parent.isLoading = false
        }

        func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
            parent.isLoading = false
        }
    }
}
