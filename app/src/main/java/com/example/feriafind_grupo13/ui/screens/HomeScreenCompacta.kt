package com.example.feriafind_grupo13.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.navigation.Screen
import androidx.navigation.NavController
import com.example.feriafind_grupo13.viewmodel.MainViewModel

@Composable
fun HomeScreenCompacta(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo que cubre toda la pantalla
        Image(
            painter = painterResource(id = R.drawable.logo), // Asegúrate de tener una imagen de fondo adecuada
            contentDescription = "Fondo de la aplicación",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop, // Escala la imagen para cubrir el espacio
            alpha = 0.2f // Le da un poco de transparencia para que el contenido resalte
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo principal
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo de FeriaFind",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Título y subtítulo
            Text(
                text = "Bienvenido a FeriaFind",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu guía para encontrar las mejores ferias locales.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Espaciador flexible para empujar los botones hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.Login) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Iniciar Sesión")
                }
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.Register) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}

