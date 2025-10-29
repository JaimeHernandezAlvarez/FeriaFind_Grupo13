package com.example.feriafind_grupo13.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Paleta para el Tema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipalOscuro,
    secondary = VerdeSecundarioOscuro,
    tertiary = NaranjaTerciarioOscuro,
    background = FondoOscuro,
    surface = SuperficieOscuro,
    onPrimary = TextoSobrePrincipalOscuro,
    onSecondary = TextoSobrePrincipalOscuro, // Reusamos el mismo para el secundario
    onTertiary = TextoSobreTerciarioOscuro,
    onBackground = TextoSobreFondoOscuro,
    onSurface = TextoSobreFondoOscuro
)

// Paleta para el Tema Claro
private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    secondary = VerdeSecundario,
    tertiary = NaranjaTerciario,
    background = FondoClaro,
    surface = SuperficieClaro,
    onPrimary = TextoSobrePrincipal,
    onSecondary = TextoSobrePrincipal, // Reusamos el texto blanco
    onTertiary = TextoSobrePrincipal, // Reusamos el texto blanco
    onBackground = TextoSobreFondoClaro,
    onSurface = TextoSobreFondoClaro
)

@Composable
fun FeriaFind_Grupo13Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}