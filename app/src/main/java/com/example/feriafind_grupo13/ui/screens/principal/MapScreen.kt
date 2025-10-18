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
import com.example.feriafind_grupo13.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    var dia by remember { mutableStateOf("Domingo") }
    var rubro by remember { mutableStateOf("Verduras") }

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
                    value = dia,
                    onValueChange = { dia = it },
                    label = { Text("Día") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = rubro,
                    onValueChange = { rubro = it },
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