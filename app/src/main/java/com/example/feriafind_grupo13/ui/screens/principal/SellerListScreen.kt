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
import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.ui.components.TarjetaVendedor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerListScreen() {
    // Datos de ejemplo
    val vendedores = listOf(
        Vendedor("101", "user1", "Juan Pérez", "Vendedor desde 2017", "9:00 - 14:00", ""),
        Vendedor("102", "user2", "Pedro Soto", "Vendedor desde 2020", "12:00 - 15:30", ""),
        Vendedor("103", "user3", "Luis Rojas", "Vendedor desde 2023", "8:30 - 13:00", "")
    )
    var searchQuery by remember { mutableStateOf("") }
    var favoritos by remember { mutableStateOf(setOf("101")) } // Juan Pérez es favorito

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vendedores") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Vendedores") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vendedores) { vendedor ->
                    TarjetaVendedor(
                        vendedor = vendedor,
                        esFavorito = vendedor.id in favoritos,
                        onFavoritoClick = {
                            favoritos = if (vendedor.id in favoritos) {
                                favoritos - vendedor.id
                            } else {
                                favoritos + vendedor.id
                            }
                        }
                    )
                }
            }
        }
    }
}