package com.example.feriafind_grupo13.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: ProductsViewModel,
    productId: Int // -1 para nuevo
) {
    // Si productId != -1, buscamos el producto en el ViewModel (estado actual)
    val productoExistente = if (productId != -1) {
        viewModel.uiState.collectAsState().value.todosLosProductos.find { it.id == productId }
    } else null

    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var precio by remember { mutableStateOf(productoExistente?.precio?.toString() ?: "") }
    var categoriaId by remember { mutableStateOf(productoExistente?.categoria?.toString() ?: "") }
    var vendedorId by remember { mutableStateOf(productoExistente?.idVendedor?.toString() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == -1) "Nuevo Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = categoriaId,
                onValueChange = { categoriaId = it },
                label = { Text("ID Categoría") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vendedorId,
                onValueChange = { vendedorId = it },
                label = { Text("ID Vendedor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val nuevoProd = Producto(
                        id = if (productId == -1) 0 else productId, // 0 para que el backend asigne ID al crear
                        nombre = nombre,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        categoria = categoriaId.toIntOrNull() ?: 0,
                        idVendedor = vendedorId.toIntOrNull() ?: 0,
                        imagenUrl = null,
                        unidadMedida = "unidad"
                    )
                    if (productId == -1) {
                        viewModel.crearProducto(nuevoProd)
                    } else {
                        viewModel.actualizarProducto(nuevoProd)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}