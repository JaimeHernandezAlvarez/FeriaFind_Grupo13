package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Ferias") },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar lógica de filtros */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filtros")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.diaSeleccionado,
                    onValueChange = viewModel::onDiaChange,
                    label = { Text("Día") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.rubroSeleccionado,
                    onValueChange = viewModel::onRubroChange,
                    label = { Text("Rubro") },
                    modifier = Modifier.weight(1f)
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder para el mapa
                Image(
                    painter = painterResource(id = R.drawable.logo), // Reemplazar con una imagen de mapa real
                    contentDescription = "Mapa Placeholder",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text("Aquí iría el mapa interactivo", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}