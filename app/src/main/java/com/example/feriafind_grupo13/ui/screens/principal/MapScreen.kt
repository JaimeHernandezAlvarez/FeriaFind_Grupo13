package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feriafind_grupo13.ui.components.ComponenteSelectorDesplegable
import com.example.feriafind_grupo13.viewmodel.MapViewModel
import com.example.feriafind_grupo13.ui.components.OsmMapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val rubros = listOf("Verduras", "Frutas", "Legumbres", "Otros")

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
                ComponenteSelectorDesplegable(
                    label = "Día",
                    selectedValue = uiState.diaSeleccionado,
                    options = diasSemana,
                    onValueChange = viewModel::onDiaChange,
                    modifier = Modifier.weight(1f)
                )
                ComponenteSelectorDesplegable(
                    label = "Rubro",
                    selectedValue = uiState.rubroSeleccionado,
                    options = rubros,
                    onValueChange = viewModel::onRubroChange,
                    modifier = Modifier.weight(1f)
                )
            }
            // --- EL RECTÁNGULO DEL MAPA ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                OsmMapView(
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Selecciona una feria en el mapa para ver detalles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}