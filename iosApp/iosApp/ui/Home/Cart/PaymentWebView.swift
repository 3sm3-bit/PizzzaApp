import SwiftUI
import WebKit

struct PaymentWebView: View {
    let url: URL
    var onSuccess: () -> Void
    var onCancel: () -> Void
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = true
    @State private var showCancelAlert = false

    var body: some View {
        NavigationView {
            ZStack {
                WebView(url: url, isLoading: $isLoading, showCancelAlert: $showCancelAlert, onSuccess: {
                    dismiss()
                    onSuccess()
                }, onCancel: {
                    dismiss()
                    onCancel()
                })
                if isLoading {
                    ProgressView()
                        .scaleEffect(1.5)
                        .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
                }
            }
            .navigationBarTitle("Pago Seguro", displayMode: .inline)
            .navigationBarItems(leading: Button(action: {
                showCancelAlert = true
            }) {
                HStack {
                    Image(systemName: "chevron.left")
                    Text("Atrás")
                }
                .foregroundColor(.uiTayRed600)
            })
            .alert(isPresented: $showCancelAlert) {
                Alert(
                    title: Text("¿Cancelar pedido?"),
                    message: Text("Si sales de la pantalla de pago, el pedido temporal será cancelado y los productos se borrarán del carrito."),
                    primaryButton: .destructive(Text("Sí, cancelar")) {
                        dismiss()
                        onCancel()
                    },
                    secondaryButton: .cancel(Text("Continuar pagando"))
                )
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct WebView: UIViewRepresentable {
    let url: URL
    @Binding var isLoading: Bool
    @Binding var showCancelAlert: Bool
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

        func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
            if let urlString = webView.url?.absoluteString {
                if urlString.contains("pizzitas://payment/cancel") || urlString.contains("pizzzaapp.com/cancel") {
                    parent.showCancelAlert = true
                }
            }
        }

        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            if let urlString = navigationAction.request.url?.absoluteString {
                if urlString.contains("pizzitas://payment/success") || urlString.contains("pizzzaapp.com/success") {
                    parent.onSuccess()
                    decisionHandler(.cancel)
                    return
                } else if urlString.contains("pizzitas://payment/cancel") || urlString.contains("pizzzaapp.com/cancel") {
                    parent.showCancelAlert = true
                    decisionHandler(.cancel)
                    return
                }
            }
            decisionHandler(.allow)
        }
    }
}
