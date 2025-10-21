package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feriafind_grupo13.ui.components.TarjetaVendedor
import com.example.feriafind_grupo13.viewmodel.SellersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerListScreen(viewModel: SellersViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vendedores") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Buscar Vendedores") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.vendedoresMostrados) { vendedor ->
                    TarjetaVendedor(
                        vendedor = vendedor,
                        esFavorito = vendedor.id in uiState.favoritos,
                        onFavoritoClick = { viewModel.onFavoritoClick(vendedor.id) }
                    )
                }
            }
        }
    }
}
