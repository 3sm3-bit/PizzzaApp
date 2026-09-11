package com.pizzza.pizzzaapp.ui

import android.util.Log
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.usecases.DataUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var dataUseCase: DataUseCase
    private lateinit var globalUiStateManager: GlobalUiStateManager
    private lateinit var viewModel: AppViewModel

    @BeforeTest
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        Dispatchers.setMain(testDispatcher)
        
        dataUseCase = mockk(relaxed = true)
        globalUiStateManager = mockk(relaxed = true)
        
        viewModel = AppViewModel(
            dataUseCase, 
            globalUiStateManager,
            ioDispatcher = testDispatcher,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `syncProducts calls usecase`() = runTest {
        // PREPARACIÓN
        coEvery { dataUseCase.syncProducts() } returns emptyList()

        // ACCIÓN
        viewModel.syncProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // VERIFICACIÓN
        coVerify { dataUseCase.syncProducts() }
    }
}
