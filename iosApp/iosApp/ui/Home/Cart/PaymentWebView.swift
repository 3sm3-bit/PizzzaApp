import SwiftUI
import WebKit

struct PaymentWebView: View {
    let url: URL
    var onSuccess: () -> Void
    var onCancel: () -> Void
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = true

    var body: some View {
        ZStack {
            WebView(url: url, isLoading: $isLoading, onSuccess: onSuccess, onCancel: onCancel)
            if isLoading {
                ProgressView()
                    .scaleEffect(1.5)
                    .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
            }
        }.uiTayHideToolbar()
    }
}

struct WebView: UIViewRepresentable {
    let url: URL
    @Binding var isLoading: Bool
    var onSuccess: () -> Void
    var onCancel: () -> Void

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
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
        var parent: WebView

        init(_ parent: WebView) {
            self.parent = parent
        }

        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            parent.isLoading = false
        }

        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            if let urlString = navigationAction.request.url?.absoluteString {
                if urlString.contains("pizzitas://payment/success") || urlString.contains("pizzzaapp.com/success") {
                    parent.onSuccess()
                    decisionHandler(.cancel)
                    return
                } else if urlString.contains("pizzitas://payment/cancel") || urlString.contains("pizzzaapp.com/cancel") {
                    parent.onCancel()
                    decisionHandler(.cancel)
                    return
                }
            }
            decisionHandler(.allow)
        }
    }
}
