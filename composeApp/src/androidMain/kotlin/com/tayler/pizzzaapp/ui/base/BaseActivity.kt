package com.tayler.pizzzaapp.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

abstract class BaseActivity : ComponentActivity() {

    @Composable
    abstract fun SetScreenConfig()
    abstract  fun setDataGlobal()
    open fun getViewModel(): BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (getViewModel()?.uiStateBase?.loading == true){

            }
            if (getViewModel()?.uiStateBase?.error == true){
            }
            SetScreenConfig()
        }
        setDataGlobal()
    }

}