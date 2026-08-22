package com.tayler.pizzzaapp.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.usecases.DataUseCase
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class OrderScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun listaDePedidos_muestraElNombreDelClienteCorrectamente() {
        // PREPARACIÓN: Creamos un mock del UseCase para alimentar al ViewModel
        val mockUseCase = mockk<DataUseCase>()
        val mockOrders = listOf(
            ParentOrderModel(
                uid = "1", 
                nameClient = "PEDIDO DE PRUEBA", 
                state = "CONFIRMADO", 
                orders = emptyList(),
                description = "", phone = "", price = "100", date = "", address = "", reception = "LOCAL"
            )
        )
        coEvery { mockUseCase.loadParentOrder(any()) } returns mockOrders
        
        val viewModel = AppViewModel(mockUseCase)
        viewModel.getGeneralOrderList()

        // ACCIÓN: Cargamos la pantalla en el test
        composeTestRule.setContent {
            OrderScreen(viewModel = viewModel, onNavigateToProducts = {})
        }

        // VERIFICACIÓN: Buscamos el texto del cliente en la pantalla
        composeTestRule.onNodeWithText("PEDIDO DE PRUEBA").assertIsDisplayed()
        composeTestRule.onNodeWithText("CONFIRMADO").assertIsDisplayed()
    }
}
