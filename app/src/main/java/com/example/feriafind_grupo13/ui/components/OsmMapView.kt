package com.example.feriafind_grupo13.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Configurar el User Agent para evitar ser bloqueado por los servidores de OSM
    // Es importante hacerlo antes de crear el MapView
    Configuration.getInstance().userAgentValue = context.packageName

    // Recordar la instancia del MapView para que no se recree en cada recomposición
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK) // Estilo de mapa estándar
            setMultiTouchControls(true) // Permitir zoom con dedos

            // Configurar punto inicial (Ej: Santiago Centro)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(-33.4489, -70.6693))
        }
    }
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach() // Limpieza de memoria al salir
        }
    }
    AndroidView(
        factory = {
            // Aquí podemos añadir marcadores iniciales
            val marcador = Marker(mapView)
            marcador.position = GeoPoint(-33.4489, -70.6693)
            marcador.title = "Feria Modelo"
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marcador)

            mapView
        },
        modifier = modifier
    )
}