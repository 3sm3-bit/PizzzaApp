import SwiftUI
import WebKit
import TaySwitfUILibrary

struct PaymentWebView: View {
    let url: URL
    var onSuccess: () -> Void
    var onCancel: () -> Void
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = true
    @State private var showCancelAlert = false

    var body: some View {
        VStack(spacing: 0) {
            // Salir solo con este botón: sin gestos de regreso, para que el usuario
            // no cierre el WebView mientras Stripe redirige a la URL de éxito.
            UiTayCToolBar(uiTayText: "Pago") { _ in
                showCancelAlert = true
            }

            ZStack {
                WebView(
                    url: url,
                    isLoading: $isLoading,
                    showCancelAlert: $showCancelAlert,
                    onSuccess: {
                        dismiss()
                        onSuccess()
                    }
                )

                if isLoading {
                    ProgressView()
                        .scaleEffect(1.5)
                        .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
                }
            }
        }
        .uiTayHideToolbar()
        .navigationBarBackButtonHidden(true)
        .edgesIgnoringSafeArea(.bottom)
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
}

struct WebView: UIViewRepresentable {
    let url: URL
    @Binding var isLoading: Bool
    @Binding var showCancelAlert: Bool
    var onSuccess: () -> Void

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        // Sin gesto atrás/adelante: podía sacar al usuario de la página de éxito de Stripe.
        webView.allowsBackForwardNavigationGestures = false
        webView.navigationDelegate = context.coordinator
        context.coordinator.observeUrl(of: webView)
        webView.load(URLRequest(url: url))
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, WKNavigationDelegate {
        var parent: WebView
        private var urlObservation: NSKeyValueObservation?
        private var didSucceed = false

        init(_ parent: WebView) {
            self.parent = parent
        }

        deinit {
            urlObservation?.invalidate()
        }

        /// Detecta también cambios de URL hechos por JavaScript (pushState),
        /// que no pasan por `decidePolicyFor`.
        func observeUrl(of webView: WKWebView) {
            urlObservation = webView.observe(\.url, options: [.new]) { [weak self] webView, _ in
                guard let urlString = webView.url?.absoluteString else { return }
                DispatchQueue.main.async {
                    _ = self?.handle(urlString)
                }
            }
        }

        /// Devuelve true si la URL es de éxito o cancelación y ya se manejó.
        private func handle(_ urlString: String) -> Bool {
            if urlString.contains("pizzitas://payment/success") || urlString.contains("pizzzaapp.com/success") {
                // La URL de éxito puede llegar por varias vías; confirmamos una sola vez.
                if !didSucceed {
                    didSucceed = true
                    parent.onSuccess()
                }
                return true
            }
            if urlString.contains("pizzitas://payment/cancel") || urlString.contains("pizzzaapp.com/cancel") {
                if !didSucceed {
                    parent.showCancelAlert = true
                }
                return true
            }
            return false
        }

        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            parent.isLoading = false
        }

        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            if let urlString = navigationAction.request.url?.absoluteString, handle(urlString) {
                decisionHandler(.cancel)
                return
            }
            decisionHandler(.allow)
        }

        /// iOS puede matar el proceso del WebView si la app pasa a segundo plano
        /// (por ejemplo, para aprobar el pago en la app del banco). Sin esto la
        /// página queda en blanco y nunca llega la redirección de éxito.
        func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
            guard !didSucceed else { return }
            parent.isLoading = true
            if webView.url != nil {
                webView.reload()
            } else {
                webView.load(URLRequest(url: parent.url))
            }
        }
    }
}
