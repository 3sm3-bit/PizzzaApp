package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.ui.AppViewModel

@Composable
fun ScreenPizza(
    viewModel: AppViewModel,
    onNavigateToDetail: () -> Unit
) {
    val uiState = viewModel.orderUiState
    var selectedSize by remember { mutableStateOf("CHICO") }
    val sizes = listOf("CHICO", "MEDIANO", "GRANDE")

    Column(modifier = Modifier.fillMaxSize()) {
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val filteredPizzas = uiState.pizzaProducts.filter { product ->
                product.tamanio.equals(selectedSize, ignoreCase = true) ||
                        (selectedSize == "CHICO" && product.tamanio.equals("CHICA", ignoreCase = true)) ||
                        (selectedSize == "MEDIANO" && product.tamanio.equals("MEDIANA", ignoreCase = true))
            }

            items(filteredPizzas) { product ->
                ProductCard(product) {
                    viewModel.selectProduct(product)
                    onNavigateToDetail()
                }
            }
        }
    }
}
