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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.data.model.Vendedor
import coil.compose.AsyncImage
import coil.request.ImageRequest


/**
 * Tarjeta reutilizable para mostrar la información de un vendedor en una lista.
 *
 * @param vendedor El objeto de datos del vendedor a mostrar.
 * @param esFavorito Booleano que indica si el vendedor está marcado como favorito.
 * @param onFavoritoClick Lambda que se ejecuta cuando el usuario presiona el ícono de favorito.
 */
@Composable
fun TarjetaVendedor(
    vendedor: Vendedor,
    esFavorito: Boolean,
    onFavoritoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- IMAGEN DEL VENDEDOR (Coil) ---
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(vendedor.fotoUrl) // URL que viene de tu API
                    .crossfade(true)
                    .error(R.drawable.logo) // Logo si falla la carga
                    .placeholder(R.drawable.logo) // Logo mientras carga
                    .build(),
                contentDescription = "Foto de ${vendedor.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.width(16.dp))

// --- INFORMACIÓN ---
            Column(modifier = Modifier.weight(1f)) {
                // Usamos 'nombreVendedor' tal como está en tu modelo
                Text(
                    text = vendedor.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Usamos el operador ?.let por si la descripción es nula
                vendedor.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }

            // --- BOTÓN FAVORITO ---
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
