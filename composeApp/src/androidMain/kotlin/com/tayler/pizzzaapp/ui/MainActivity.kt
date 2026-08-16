package com.tayler.pizzzaapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tayler.pizzzaapp.ui.base.BaseActivity
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()

    @Composable
    override fun SetScreenConfig() {
        App(viewModel)

    }

    override fun setDataGlobal() {
          viewModel.getOrderList()
    }

    override fun getViewModel(): BaseViewModel = viewModel
}

@Composable
fun App(viewModel : AppViewModel) {
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