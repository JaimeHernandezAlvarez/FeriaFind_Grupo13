package com.example.feriafind_grupo13.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.data.model.Vendedor

@Composable
fun SellerCard(
    vendedor: Vendedor,
    esFavorito: Boolean,
    onFavoritoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Reemplazar con la foto del vendedor
                contentDescription = vendedor.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vendedor.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = vendedor.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                vendedor.horario?.let { horario ->
                    Text(
                        text = "Horario: $horario",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            IconButton(onClick = onFavoritoClick) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

