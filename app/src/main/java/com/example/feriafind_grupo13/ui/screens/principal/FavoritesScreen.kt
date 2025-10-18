package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.ui.components.TarjetaVendedor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen() {
    // Datos de ejemplo (los mismos que en SellerListScreen para simular)
    val todosLosVendedores = listOf(
        Vendedor("101", "user1", "Juan Pérez", "Vendedor desde 2017", "9:00 - 14:00", ""),
        Vendedor("102", "user2", "Pedro Soto", "Vendedor desde 2020", "12:00 - 15:30", ""),
        Vendedor("103", "user3", "Luis Rojas", "Vendedor desde 2023", "8:30 - 13:00", "")
    )
    var favoritos by remember { mutableStateOf(setOf("101")) }
    val vendedoresFavoritos = todosLosVendedores.filter { it.id in favoritos }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Favoritos") })
        }
    ) { innerPadding ->
        if (vendedoresFavoritos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no tienes vendedores favoritos.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vendedoresFavoritos) { vendedor ->
                    TarjetaVendedor(
                        vendedor = vendedor,
                        esFavorito = true,
                        onFavoritoClick = {
                            favoritos = favoritos - vendedor.id
                        }
                    )
                }
            }
        }
    }
}