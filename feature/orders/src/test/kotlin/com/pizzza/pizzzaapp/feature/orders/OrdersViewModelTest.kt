package com.pizzza.pizzzaapp.feature.orders

import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity
import com.pizzza.pizzzaapp.usecases.DataUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var dataUseCase: DataUseCase
    private lateinit var globalUiStateManager: GlobalUiStateManager
    private lateinit var appDataOrder: AppDataOrder
    private lateinit var viewModel: OrdersViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        dataUseCase = mockk(relaxed = true)
        globalUiStateManager = mockk(relaxed = true)
        appDataOrder = AppDataOrder()
        
        viewModel = OrdersViewModel(
            dataUseCase, 
            globalUiStateManager, 
            appDataOrder,
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
    fun `getGeneralOrderList updates appDataOrder with sorted orders`() = runTest {
        // PREPARACIÓN
        val user = UserEntity(
            uid = "user123",
            nameUser = "testuser",
            names = "Test",
            lastName = "User",
            document = "123",
            email = "test@pizzza.com",
            phone = "123456789",
            address = "Address",
            rol = "client",
            area = "Area",
            longitude = "0.0",
            latitude = "0.0",
            token = "token"
        )
        coEvery { dataUseCase.getUserLocal() } returns user
        
        val mockOrders = listOf(
            createMockOrder("1", "LISTO"),
            createMockOrder("2", "CONFIRMADO")
        )
        coEvery { dataUseCase.loadParentOrder(userId = "user123") } returns mockOrders

        // ACCIÓN
        viewModel.getGeneralOrderList(forceLoading = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // VERIFICACIÓN
        val state = appDataOrder.state.value
        assertEquals(2, state.orders.size)
        // Check sorting: CONFIRMADO (1) before LISTO (2)
        assertEquals("2", state.orders[0].uid)
        assertEquals("1", state.orders[1].uid)
        assertTrue(state.ordersLoaded)
    }

    @Test
    fun `getOrderDetail updates selectedOrder`() = runTest {
        // PREPARACIÓN
        val mockOrder = createMockOrder("123", "CONFIRMADO")
        coEvery { dataUseCase.getOrderById("123") } returns mockOrder

        // ACCIÓN
        viewModel.getOrderDetail("123")
        testDispatcher.scheduler.advanceUntilIdle()

        // VERIFICACIÓN
        assertEquals("123", appDataOrder.state.value.selectedOrder?.uid)
    }

    @Test
    fun `addToCart adds item and updates quantity if already exists`() = runTest {
        // PREPARACIÓN
        val product = com.pizzza.pizzzaapp.model.ProductModel(
            uid = "p1",
            nameProduct = "Pizza",
            description = "Desc",
            price = "10.0",
            urlImg = "",
            currency = "USD",
            currencySymbol = "$",
            state = true,
            type = "pizza",
            tamanio = "Large",
            priceChosse = "0.0"
        )

        // ACCIÓN 1: Add new item
        viewModel.addToCart(product, 1, "Thin", false, "No onions")
        
        // VERIFICACIÓN 1
        assertEquals(1, appDataOrder.state.value.cart.size)
        assertEquals(1, appDataOrder.state.value.cart[0].quantity)

        // ACCIÓN 2: Add same item again
        viewModel.addToCart(product, 2, "Thin", false, "No onions")

        // VERIFICACIÓN 2
        assertEquals(1, appDataOrder.state.value.cart.size)
        assertEquals(3, appDataOrder.state.value.cart[0].quantity)
    }

    private fun createMockOrder(uid: String, state: String) = ParentOrderModel(
        uid = uid,
        nameClient = "Client $uid",
        description = "Desc $uid",
        price = "10.0",
        phone = "123",
        date = "2023-01-01",
        state = state,
        address = "Address",
        reception = "DELIVERY",
        symbol = "$",
        branchId = "branch1",
        stage = "1",
        latitude = "0.0",
        longitude = "0.0",
        currentLatitude = "0.0",
        currentLongitude = "0.0",
        statePay = "PAID",
        userId = "user123",
        driverId = "driver1",
        orders = emptyList()
    )
}
