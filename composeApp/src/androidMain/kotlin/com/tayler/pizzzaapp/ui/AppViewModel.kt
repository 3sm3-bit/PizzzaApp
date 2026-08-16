package com.tayler.pizzzaapp.ui

import android.app.Application
import android.util.Log
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.tayler.pizzzaapp.usecases.DataUseCase

class AppViewModel(
    private val dataUseCase: DataUseCase
) : BaseViewModel() {

    fun getOrderList(){
        execute {
            val responde = dataUseCase.loadOrder()
            Log.d("dataservice",responde.toString())
        }
    }
}