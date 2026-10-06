//
//  InfoDetailView.swift
//  iosApp
//

import SwiftUI
import WebKit
import TaySwitfUILibrary

struct InfoDetailView: View {
    let title: String
    let htmlContent: String
    @Environment(\.dismiss) var dismiss

    var body: some View {
        VStack(spacing: 0) {
            UiTayCToolBar(uiTayText: title) { _ in
                dismiss()
            }
        
            GeometryReader { geometry in
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 16) {
                        HTMLContentView(htmlString: htmlContent)
                    }
                    .padding(20)
                    .frame(
                        maxWidth: .infinity,
                        minHeight: geometry.size.height - 40,
                        alignment: .topLeading
                    )
                    .background(Color.white)
                    .cornerRadius(16)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.uiTayRed50, lineWidth: 3)
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .uiTayHideToolbar()
        .background(Color.white)
    }
}

private struct HTMLContentView: UIViewRepresentable {
    let htmlString: String

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.isOpaque = false
        webView.backgroundColor = .clear
        webView.scrollView.isScrollEnabled = true
        
        let styledHtml = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                    font-size: 14px;
                    line-height: 1.6;
                    color: #4A4A4A;
                    margin: 0;
                    padding: 0;
                    background-color: transparent;
                }
                b { color: #111111; }
                a { color: #E53935; text-decoration: underline; }
            </style>
        </head>
        <body>
            \(htmlString)
        </body>
        </html>
        """
        
        webView.loadHTMLString(styledHtml, baseURL: nil)
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}
}
