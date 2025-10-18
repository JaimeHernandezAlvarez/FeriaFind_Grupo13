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
import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.ui.components.TarjetaProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen() {
    // Datos de ejemplo. En una app real, vendrían de un ViewModel.
    val productos = listOf(
        Producto("1", "Tomates", 1200.0, "Verduras", "", "101"),
        Producto("2", "Naranjas", 1100.0, "Frutas", "", "102"),
        Producto("3", "Lechugas", 500.0, "Verduras", "", "103")
    )

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Productos") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Productos") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productos) { producto ->
                    // Aquí necesitaríamos una forma de obtener los nombres, por ahora usamos IDs.
                    TarjetaProducto(
                        producto = producto,
                        nombreCategoria = producto.categoria,
                        nombreVendedor = "Vendedor ${producto.idVendedor}"
                    )
                }
            }
        }
    }
}