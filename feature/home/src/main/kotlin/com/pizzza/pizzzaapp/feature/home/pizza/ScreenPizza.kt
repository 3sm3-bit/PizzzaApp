package com.pizzza.pizzzaapp.feature.home.pizza

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzaapp.feature.home.FilterChipSurface
import com.pizzza.pizzzaapp.feature.home.ProductCard
import com.pizzza.pizzzaapp.feature.home.PromotionsBanner
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.pizzza.pizzzaapp.feature.home.PizzaGridCard
import com.pizzza.pizzzaapp.feature.orders.OrdersViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScreenPizza(
    onNavigateToDetail: () -> Unit,
) {

    val viewModel: OrdersViewModel = koinViewModel()
    val uiState by LocalAppDataOrder.current.state.collectAsStateWithLifecycle()
    var selectedSize by remember { mutableStateOf("GRANDE") }
    val sizes = listOf("GRANDE", "MEDIANO", "CHICO")

    Column(modifier = Modifier.fillMaxSize()) {
        PromotionsBanner(promotions = uiState.promotionsProducts) { product ->
            viewModel.selectProduct(product)
            onNavigateToDetail()
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sizes) { size ->
                val isSelected = selectedSize == size
                FilterChipSurface(
                    text = size,
                    isSelected = isSelected,
                    onClick = { selectedSize = size }
                )
            }
        }

        val filteredPizzas = uiState.pizzaProducts.filter { product ->
            product.tamanio.equals(selectedSize, ignoreCase = true) ||
                    (selectedSize == "CHICO" && product.tamanio.equals("CHICA", ignoreCase = true)) ||
                    (selectedSize == "MEDIANO" && product.tamanio.equals("MEDIANA", ignoreCase = true))
        }

        if (uiState.promotionsProducts.isNotEmpty()) {
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPizzas.size) { index ->
                    val product = filteredPizzas[index]
                    PizzaGridCard(product) {
                        viewModel.selectProduct(product)
                        onNavigateToDetail()
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPizzas) { product ->
                    ProductCard(product) {
                        viewModel.selectProduct(product)
                        onNavigateToDetail()
                    }
                }
            }
        }
    }
}
