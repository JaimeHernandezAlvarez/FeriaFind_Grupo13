package com.example.feriafind_grupo13.ui.screens.principal

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- FOTO ---
                var tempUri by remember { mutableStateOf<Uri?>(null) }

                fun createTempUri(): Uri {
                    val tmpFile = File.createTempFile("profile_pic", ".jpg", context.cacheDir).apply {
                        createNewFile()
                        deleteOnExit()
                    }
                    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tmpFile)
                }

                val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                    if (success && tempUri != null) viewModel.onFotoChange(tempUri)
                }
                val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) viewModel.onFotoChange(uri)
                }
                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        tempUri = createTempUri()
                        cameraLauncher.launch(tempUri!!)
                    }
                }

                SelectorImagenPerfil(
                    fotoUri = uiState.fotoUri,
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onCameraClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            tempUri = createTempUri()
                            cameraLauncher.launch(tempUri!!)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )

                // --- CAMPOS ---
                CampoDeTextoAuth(uiState.nombre, viewModel::onNombreChange, "Nombre", false, "")

                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = { Text("Descripción / Bio") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = {},
                    label = { Text("Correo (Fijo)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )

                CampoDeTextoAuth(uiState.horario, viewModel::onHorarioChange, "Horario", false, "")

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    BotonPerfil("Restaurar", viewModel::restaurarValores, Modifier.weight(1f), false)
                    BotonPerfil("Guardar", viewModel::guardarCambios, Modifier.weight(1f), true)
                }

                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Cuenta", color = Color.White)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción es irreversible. Se borrarán todos tus datos del servidor.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.eliminarCuenta(onSuccess = onLogout)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}