package com.pizzza.pizzzaapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.pizzza.pizzzaapp.BuildConfig
import com.pizzza.pizzzaapp.R
import com.pizzza.pizzzaapp.component.AppNavigation
import com.pizzza.pizzzaapp.core.ui.base.BaseActivity
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.utils.SecurityUtils
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {

    private val cartViewModel: CartViewModel by inject()
    companion object {
        private const val UPDATE_CODE = 100
    }
    private val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()

    var securityWarningState by mutableStateOf(SecurityUtils.securityWarning)

    @Composable
    override fun SetScreenConfig() {
        val appDataOrder: AppDataOrder = koinInject()
        val warning = securityWarningState
        
        if (warning != SecurityUtils.SecurityWarningType.NONE) {
            val (titleRes, subTitleRes) = when (warning) {
                SecurityUtils.SecurityWarningType.ROOT -> 
                    R.string.text_error_root to R.string.text_error_message_root
                SecurityUtils.SecurityWarningType.DEVELOPER_MODE -> 
                    R.string.text_error_developer to R.string.text_error_message_developer
                SecurityUtils.SecurityWarningType.EMULATOR -> 
                    R.string.text_error_emulator to R.string.text_error_message_emulator
            }
            RenderGenericDialog(
                image = R.drawable.ic_info_error,
                title = getString(titleRes),
                subTitle = getString(subTitleRes)
            ) {
                exitProcess(0)
            }
        } else {
            CompositionLocalProvider(LocalAppDataOrder provides appDataOrder) {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "pizzitas" && data.host == "payment") {
            if (data.path == "/success") {
                cartViewModel.confirmOrder(statePay = "PAGADO") {
                    val newIntent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(newIntent)
                }
            }
        }
    }

    override fun setDataGlobal() {
        intent?.let { handleIntent(intent) }
        window.decorView.post {
            validateVersionUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        SecurityUtils.verifyIntegrity(this, BuildConfig.DEBUG)
        securityWarningState = SecurityUtils.securityWarning
    }

    private fun validateVersionUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        updateOptions,
                        UPDATE_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            //not code
        }
    }
}
