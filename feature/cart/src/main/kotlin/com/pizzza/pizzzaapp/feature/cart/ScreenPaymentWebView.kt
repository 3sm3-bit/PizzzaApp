package com.pizzza.pizzzaapp.feature.cart

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.valu.uitaycompose.utils.tay_red_600

@Composable
fun ScreenPaymentWebView(
    url: String,
    onSuccess: () -> Unit,
    onCancelOrder: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var showCancelDialog by remember { mutableStateOf(false) }

    BackHandler {
        showCancelDialog = true
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text("¿Cancelar pedido?")
            },
            text = {
                Text("Si sales de la pantalla de pago, el pedido temporal será cancelado y los productos se borrarán del carrito.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelOrder()
                    }
                ) {
                    Text("Sí, cancelar", color = tay_red_600)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text("Continuar pagando", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(top= 24.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        private fun checkUrl(urlStr: String): Boolean {
                            if (urlStr.contains("pizzitas://payment/success") || urlStr.contains("pizzzaapp.com/success")) {
                                onSuccess()
                                return true
                            } else if (urlStr.contains("pizzitas://payment/cancel") || urlStr.contains("pizzzaapp.com/cancel")) {
                                showCancelDialog = true
                                return true
                            }
                            return false
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            checkUrl(url ?: "")
                        }

                        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                            super.doUpdateVisitedHistory(view, url, isReload)
                            checkUrl(url ?: "")
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val newUrl = request?.url?.toString() ?: ""
                            return checkUrl(newUrl)
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = tay_red_600)
                }
            }
        }
    }
}
