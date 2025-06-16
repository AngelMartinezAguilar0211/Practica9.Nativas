// MainActivity.kt
package com.example.practica9nativas.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import kotlinx.coroutines.launch
import android.widget.Toast
import android.Manifest
import androidx.compose.ui.platform.LocalContext
import com.example.core.HydrationDataStore
import com.example.core.WaterCountRepository
import com.google.firebase.database.ValueEventListener


class MainActivity : ComponentActivity() {
    // Inicializar DataStore
    private lateinit var hydrationDataStore: HydrationDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crear instancia del DataStore
        hydrationDataStore = HydrationDataStore(this)
        // Solicitud de permisos para Firebase Analytics
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
    var waterCount by remember { mutableIntStateOf(0) }
    var isUpdating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var firebaseListener by remember { mutableStateOf<ValueEventListener?>(null) }

    //val waterCount by hydrationDataStore.waterCountFlow.collectAsState(initial = 0)
    // Mantener el listener vivo durante el Composable
    LaunchedEffect(Unit) {
        hydrationDataStore.waterCountFlow.collect { count ->
            if (!isUpdating && count != waterCount) {
                waterCount = count
            }
        }
    }
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
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Icon(Icons.Default.LocalDrink, contentDescription = "Agua", modifier = Modifier.size(48.dp)) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Text("Vasos de agua hoy: $waterCount", style = MaterialTheme.typography.titleMedium) }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
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
                    Icon(Icons.Default.Remove, contentDescription = "Restar vaso", modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(24.dp))
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar vaso", modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}
