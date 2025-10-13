package com.example.feriafind_grupo13.ui.screens.autenticacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feriafind_grupo13.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("FeriaFind",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            ) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally// nos ayuda a centrar los textos
        ) {
            Text(
                text = "Bienvenido a FeriaFind",
                style = MaterialTheme.typography.headlineMedium, // Con esto podemos controlar el tamano de nuestro texto
                color = MaterialTheme.colorScheme.secondary //  y con esto controlamos el color
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // espacio entre botones
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /*acción*/ },
                    modifier = Modifier.weight(1f), // hace que ocupen el mismo espacio
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Iniciar Sesión")
                }

                Button(
                    onClick = { /*acción*/ },
                    modifier = Modifier.weight(1f), // mismo tamaño
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Registrarse")
                }
            }

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/*@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Light - Compact")
@Composable
fun PreviewAdaptativaLight(

) {
    FeriaFind_Grupo13Theme(darkTheme = false) {
        HomeScreenAdaptativa()
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Dark - Compact")
@Composable
fun PreviewAdaptativaDark(
) {
    FeriaFind_Grupo13Theme(darkTheme = true) {
        HomeScreenAdaptativa()
    }
}*/
