package com.example.feriafind_grupo13.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.viewmodel.SellersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSellerScreen(
    navController: NavController,
    viewModel: SellersViewModel,
    sellerId: String // Vacío para nuevo
) {
    val vendedorExistente = if (sellerId.isNotBlank()) {
        viewModel.uiState.collectAsState().value.todosLosVendedores.find { it.id == sellerId }
    } else null

    var nombre by remember { mutableStateOf(vendedorExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(vendedorExistente?.descripcion ?: "") }
    var idUsuario by remember { mutableStateOf(vendedorExistente?.idUsuario ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (sellerId.isBlank()) "Nuevo Vendedor" else "Editar Vendedor") },
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
                label = { Text("Nombre del Vendedor/Tienda") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            if(sellerId.isBlank()){
                OutlinedTextField(
                    value = idUsuario,
                    onValueChange = { idUsuario = it },
                    label = { Text("ID Usuario Asociado") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    val nuevoVend = Vendedor(
                        id = if (sellerId.isBlank()) "" else sellerId,
                        idUsuario = idUsuario,
                        nombre = nombre,
                        descripcion = descripcion,
                        fotoUrl = null,
                        horario = null
                    )
                    if (sellerId.isBlank()) {
                        viewModel.crearVendedor(nuevoVend)
                    } else {
                        viewModel.actualizarVendedor(nuevoVend)
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