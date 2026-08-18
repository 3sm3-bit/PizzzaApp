package com.tayler.pizzzaapp.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

abstract class BaseActivity : ComponentActivity() {

    @Composable
    abstract fun SetScreenConfig()
    abstract  fun setDataGlobal()
    open fun getViewModel(): BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState = getViewModel()?.uiStateBase
            
            Box(modifier = Modifier.fillMaxSize()) {
                SetScreenConfig()

                if (uiState?.loading == true) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
                
                if (uiState?.error == true) {
                    AlertDialog(
                        onDismissRequest = { getViewModel()?.uiStateBase = uiState.copy(error = false) },
                        title = { Text("Error") },
                        text = { Text(uiState.errorType.message ?: "Ocurrió un error inesperado") },
                        confirmButton = {
                            TextButton(onClick = { getViewModel()?.uiStateBase = uiState.copy(error = false) }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
        setDataGlobal()
    }

}