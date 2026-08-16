package com.tayler.pizzzaapp.ui

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.messaging.FirebaseMessaging
import com.tayler.pizzzaapp.ui.base.BaseActivity
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.valu.uitaycompose.utils.permission.rememberUiTayPermissionManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()

    @Composable
    override fun SetScreenConfig() {
        App(viewModel)
    }

    override fun setDataGlobal() {
          viewModel.getOrderList()
          printFirebaseToken()
    }

    private fun printFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "Current token: $token")
            println("FCM Current token: $token")
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel
}

@Composable
fun App(viewModel : AppViewModel) {
    val permissionManager = rememberUiTayPermissionManager()

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
                Log.d("FCM", "Notification permission granted")
            }
        }
    }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


        }
    }
}
