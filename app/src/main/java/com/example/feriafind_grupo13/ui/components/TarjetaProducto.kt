package com.example.feriafind_grupo13.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.data.model.Producto

/**
 * Tarjeta reutilizable para mostrar la información de un producto con una imagen grande en la parte superior.
 *
 * @param producto El objeto de datos del producto a mostrar.
 * @param nombreCategoria El nombre resuelto de la categoría (no el ID).
 * @param nombreVendedor El nombre resuelto del vendedor (no el ID).
 */
@Composable
fun TarjetaProducto(
    producto: Producto,
    nombreCategoria: String,
    nombreVendedor: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Imagen del Producto
            Image(
                // TODO: Reemplazar 'R.drawable.logo' con la imagen real del producto.
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Imagen de ${producto.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            // Columna con los detalles del producto
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Formateo del precio para mostrar puntos para los miles, ej: "1.200"
                Text(
                    text = "$${"%,.0f".format(producto.precio).replace(",", ".")}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Categoría: $nombreCategoria",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Vendedor: $nombreVendedor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}



