package com.example.feriafind_grupo13.ui.screens.principal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.feriafind_grupo13.ui.components.BotonPerfil
import com.example.feriafind_grupo13.ui.components.CampoDeTextoAuth
import com.example.feriafind_grupo13.ui.components.SelectorImagenPerfil
import com.example.feriafind_grupo13.viewmodel.ProfileViewModel
import java.io.File

private const val TAG = "ProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel,onLogout: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // --- Snackbar para mensajes de éxito o error ---
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Función auxiliar para fotos temporales
    fun getTmpFileUri(context: Context): Uri? {
        return try {
            val tmpFile = File.createTempFile("tmp_image_", ".png", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            val authority = "${context.packageName}.provider"
            FileProvider.getUriForFile(context, authority, tmpFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating temp file URI: ${e.message}", e)
            null // Devolver null si hay error
        }
    }
    // Launchers de cámara y galería
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.onFotoChange(uri)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempUri?.let { viewModel.onFotoChange(it) }
        }
        tempUri = null // Limpiar la URI temporal después del intento
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            tempUri = getTmpFileUri(context)
            tempUri?.let { cameraLauncher.launch(it) }
        } else {
            Log.e(TAG, "Permiso de cámara denegado.")
        }
    }

    val launchGallery = { galleryLauncher.launch("image/*") }
    val launchCamera = {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            tempUri = getTmpFileUri(context)
            tempUri?.let { cameraLauncher.launch(it) }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectorImagenPerfil(
                fotoUri = uiState.fotoUri,
                onGalleryClick = launchGallery,
                onCameraClick = { launchCamera() }
            )

            CampoDeTextoAuth(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = "Nombre",
                isError = false,
                errorMessage = ""
            )
            CampoDeTextoAuth(
                value = uiState.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = "Descripción",
                isError = false,
                errorMessage = ""
            )
            OutlinedTextField(
                value = uiState.correo,
                onValueChange = { /* No editable */ },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            CampoDeTextoAuth(
                value = uiState.horario,
                onValueChange = viewModel::onHorarioChange,
                label = "Horario",
                isError = false,
                errorMessage = ""
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BotonPerfil(
                    text = "Restaurar",
                    onClick = viewModel::restaurarValores,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
                BotonPerfil(
                    text = "Guardar",
                    onClick = viewModel::guardarCambios,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // Zona de Peligro: Eliminar
            Button(
                onClick = { mostrarDialogoEliminar = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar Cuenta", color = Color.White)
            }
        }
    }

    // Diálogo de Confirmación de Borrado
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("¿Estás seguro?") },
            text = { Text("Si eliminas tu cuenta, perderás todos tus datos y favoritos. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        viewModel.eliminarCuenta(onSuccess = onLogout)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar Definitivamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

