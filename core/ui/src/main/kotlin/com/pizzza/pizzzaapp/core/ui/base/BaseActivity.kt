package com.pizzza.pizzzaapp.core.ui.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pizzza.pizzzaapp.core.ui.PizzzaTheme
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.singleton.LocalGlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.usecases.DataUseCase
import com.valu.uitaycompose.modal.UiTayDialog
import com.valu.uitaycompose.loading.UiProgress
import com.valu.uitaycompose.model.UiTayDialogModel
import com.valu.uitaycompose.utils.tay_red_600
import org.koin.android.ext.android.inject
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.repository.network.exception.UiTayApiException
import com.pizzza.pizzzaapp.repository.network.exception.UnAuthorizedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseActivity : ComponentActivity() {

    val globalUiStateManager: GlobalUiStateManager by inject()
    val dataUseCase: DataUseCase by inject()
    val appDataOrder: AppDataOrder by inject()

    @Composable
    fun RenderGenericDialog(
        image: Int,
        title: String,
        subTitle: String,
        onResult: (Boolean) -> Unit
    ) {
        UiTayDialog(
            model = UiTayDialogModel(
                image = image,
                title = title,
                subTitle = subTitle,
                isCancel = false
            ),
            onDismissRequest = { result: Boolean ->
                onResult(result)
            }
        )
    }

    @Composable
    abstract fun SetScreenConfig()
    abstract fun setDataGlobal()

    private fun handleUnauthorized() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                dataUseCase.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            appDataOrder.reset()
            withContext(Dispatchers.Main) {
                val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                if (intent != null) {
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            PizzzaTheme {
                CompositionLocalProvider(
                    LocalGlobalUiStateManager provides globalUiStateManager
                ) {
                    val uiState by globalUiStateManager.uiState.collectAsStateWithLifecycle()

                    Box(modifier = Modifier.fillMaxSize()) {
                        SetScreenConfig()

                        if (uiState.loading) {
                            UiProgress(colorProgress = tay_red_600)
                        }

                        if (uiState.error) {
                            val isUnauthorized = uiState.errorType is UnAuthorizedException ||
                                    (uiState.errorType is UiTayApiException && (uiState.errorType as UiTayApiException).code == 401)

                            val errorInfo = uiState.errorType.mapperError()
                            RenderGenericDialog(
                                image = errorInfo.first,
                                title = errorInfo.second,
                                subTitle = errorInfo.third
                            ) { dialogResult ->
                                globalUiStateManager.updateUiState { current ->
                                    current.copy(
                                        error = false,
                                        popUpGeneric = true,
                                        popUpGenericValue = dialogResult
                                    )
                                }
                                if (isUnauthorized) {
                                    handleUnauthorized()
                                }
                            }
                        }
                    }
                }
            }
        }
        setDataGlobal()
    }
}

fun Throwable.mapperError(): Triple<Int, String, String> {
    return when (this) {
        is UnAuthorizedException -> {
            Triple(
                R.drawable.ic_pizzza,
                "Sesión Expirada",
                "Tu sesión ha caducado. Por favor, vuelve a iniciar sesión."
            )
        }
        is UiTayApiException -> {
            if (this.code == 401) {
                Triple(
                    R.drawable.ic_pizzza,
                    "Sesión Expirada",
                    "Tu sesión ha caducado. Por favor, vuelve a iniciar sesión."
                )
            } else {
                Triple(
                    R.drawable.ic_pizzza,
                    this.title.ifEmpty { "Error" },
                    this.messageApi.ifEmpty { "Ocurrió un error inesperado" }
                )
            }
        }
        else -> {
            Triple(
                R.drawable.ic_pizzza,
                "Error",
                this.message ?: "Ocurrió un error inesperado"
            )
        }
    }
}
