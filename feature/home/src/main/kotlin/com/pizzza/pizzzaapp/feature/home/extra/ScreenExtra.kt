package com.pizzza.pizzzaapp.feature.home.extra

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzaapp.feature.home.ExtraProductCard
import com.pizzza.pizzzaapp.feature.home.FilterChipSurface
import com.pizzza.pizzzaapp.feature.home.PromotionsBanner
import com.pizzza.pizzzaapp.feature.home.HomeViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder

@Composable
fun ScreenExtra(
    viewModel: HomeViewModel,
    onNavigateToDetail: () -> Unit,
) {
    val uiState by LocalAppDataOrder.current.state.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("TODOS") }
    val categories = listOf("TODOS", "EXTRAS", "BEBIDAS")

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
