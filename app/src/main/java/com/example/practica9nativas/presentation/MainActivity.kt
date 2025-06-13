// MainActivity.kt
package com.example.practica9nativas.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.TimeText
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    // Inicializar el DataStore
    private lateinit var hydrationDataStore: HydrationDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crear instancia del DataStore
        hydrationDataStore = HydrationDataStore(this)
        setContent {
            MaterialTheme {
                HydrationReminderScreen(hydrationDataStore)
            }
        }
    }
}

@Composable
fun HydrationReminderScreen(hydrationDataStore: HydrationDataStore) {
    val scope = rememberCoroutineScope()
    val waterCount by hydrationDataStore.waterCountFlow.collectAsState(initial = 0)

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //item { TimeText() }
        //item { Spacer(modifier = Modifier.height(4.dp)) }
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
                            scope.launch { hydrationDataStore.saveWaterCount(waterCount - 1) }
                        }
                    }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar vaso", modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(24.dp))
                IconButton(
                    onClick = {
                        scope.launch { hydrationDataStore.saveWaterCount(waterCount + 1) }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vaso", modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}
