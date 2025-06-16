package com.example.practica9nativasandroid

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.core.HydrationDataStore
import kotlinx.coroutines.launch
import com.example.core.WaterCountRepository
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private lateinit var hydrationDataStore: HydrationDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hydrationDataStore = HydrationDataStore(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if(!isGranted){
                    Toast.makeText(this, "Permiso requerido para recibir notificaciones", Toast.LENGTH_SHORT).show()
                }
            }

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            MaterialTheme {
                HydrationReminderScreen(hydrationDataStore)
            }
        }
    }
}

@Composable
fun HydrationReminderScreen(hydrationDataStore: HydrationDataStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isUpdating by remember { mutableStateOf(false) }
    // Estado principal: valor local, sincronizado con DataStore y Firebase
    var waterCount by remember { mutableIntStateOf(0) }

    // Mantiene referencia al listener de Firebase para limpiar después
    var firebaseListener by remember { mutableStateOf<ValueEventListener?>(null) }

    // 1. Escuchar cambios locales (DataStore) al iniciar (solo primera vez)
    LaunchedEffect(Unit) {
        hydrationDataStore.waterCountFlow.collect { count ->
            if (!isUpdating && count != waterCount) {
                waterCount = count
            }
        }
    }

    // 2. Escuchar cambios de Firebase y sincronizar ambos
    DisposableEffect(Unit) {
        val listener = WaterCountRepository.observeWaterCount { newValue ->
            if (newValue != waterCount) {
                scope.launch {
                    isUpdating = true
                    hydrationDataStore.saveWaterCount(newValue)
                    waterCount = newValue
                    isUpdating = false
                }
            }
        }
        firebaseListener = listener
        onDispose {
            WaterCountRepository.removeListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.LocalDrink,
            contentDescription = "Agua",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Vasos de agua hoy: $waterCount",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (waterCount > 0) {
                        val newValue = waterCount - 1
                        waterCount = newValue
                        scope.launch {
                            isUpdating = true
                            hydrationDataStore.saveWaterCount(newValue)
                            WaterCountRepository.setWaterCount(newValue)
                            isUpdating = false
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Restar vaso",
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            IconButton(
                onClick = {
                    val newValue = waterCount + 1
                    waterCount = newValue
                    scope.launch {
                        isUpdating = true
                        hydrationDataStore.saveWaterCount(newValue)
                        WaterCountRepository.setWaterCount(newValue)
                        isUpdating = false
                    }
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar vaso",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
