package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.ui.AppViewModel

@Composable
fun ScreenExtra(
    viewModel: AppViewModel,
    onNavigateToDetail: () -> Unit
) {
    val uiState = viewModel.orderUiState
    var selectedCategory by remember { mutableStateOf("TODOS") }
    val categories = listOf("TODOS", "EXTRAS", "BEBIDAS")

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChipSurface(
                    text = cat,
                    isSelected = isSelected,
                    onClick = { selectedCategory = cat }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val filteredExtras = uiState.extraProducts.filter { product ->
                when (selectedCategory) {
                    "EXTRAS" -> product.type == "2"
                    "BEBIDAS" -> product.type == "3"
                    else -> true
                }
            }

            items(filteredExtras) { product ->
                ExtraProductCard(product) {
                    viewModel.selectProduct(product)
                    onNavigateToDetail()
                }
            }
        }
    }
}
