package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.TarjetaVendedor
import com.example.feriafind_grupo13.viewmodel.SellersViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerListScreen(viewModel: SellersViewModel,navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vendedores") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditSeller.createRoute("")) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Vendedor", tint = MaterialTheme.colorScheme.onPrimary)
            }
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

            when {
                uiState.isLoading -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                uiState.errorMessage != null -> { Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error) }
                uiState.vendedoresMostrados.isEmpty() -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron vendedores.") } }
                else -> { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.vendedoresMostrados) { vendedor ->
                            TarjetaVendedor(
                                vendedor = vendedor,
                                esFavorito = vendedor.id.toString() in uiState.favoritos,
                                onFavoritoClick = { viewModel.onFavoritoClick(vendedor.id.toString()) },
                                onEditClick = { navController.navigate(Screen.AddEditSeller.createRoute(vendedor.id)) },
                                onDeleteClick = { viewModel.eliminarVendedor(vendedor.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}