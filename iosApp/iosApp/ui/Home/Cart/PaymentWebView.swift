import SwiftUI
import WebKit

struct PaymentWebView: View {
    let url: URL
    var onSuccess: () -> Void
    var onCancel: () -> Void
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = true
    @State private var showCancelAlert = false
    @State private var isPaymentSuccessful = false

    var body: some View {
        ZStack {
            SwipeBackDetector {
                showCancelAlert = true
            }
            .frame(width: 0, height: 0)

            WebView(
                url: url,
                isLoading: $isLoading,
                showCancelAlert: $showCancelAlert,
                onSuccess: {
                    isPaymentSuccessful = true
                    dismiss()
                    onSuccess()
                },
                onCancel: {
                    dismiss()
                    onCancel()
                }
            )
            
            if isLoading {
                ProgressView()
                    .scaleEffect(1.5)
                    .progressViewStyle(CircularProgressViewStyle(tint: .uiTayRed600))
            }
        }
        .uiTayHideToolbar()
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

struct SwipeBackDetector: UIViewRepresentable {
    var onSwipeBack: () -> Void

    func makeUIView(context: Context) -> UIView {
        let view = UIView(frame: .zero)
        let recognizer = UIScreenEdgePanGestureRecognizer(target: context.coordinator, action: #selector(Coordinator.handleSwipe(_:)))
        recognizer.edges = .left
        recognizer.delegate = context.coordinator
        
        DispatchQueue.main.async {
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let window = windowScene.windows.first(where: { $0.isKeyWindow }) {
                window.addGestureRecognizer(recognizer)
            }
        }
        
        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onSwipeBack: onSwipeBack)
    }

    class Coordinator: NSObject, UIGestureRecognizerDelegate {
        var onSwipeBack: () -> Void

        init(onSwipeBack: @escaping () -> Void) {
            self.onSwipeBack = onSwipeBack
        }

        @objc func handleSwipe(_ recognizer: UIScreenEdgePanGestureRecognizer) {
            if recognizer.state == .began {
                onSwipeBack()
            }
        }

        func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
            return true
        }
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
