// HydrationDataStore.kt
package com.example.practica9nativas.presentation

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "hydration_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object HydrationPreferences {
    val WATER_COUNT = intPreferencesKey("water_count")
}

class HydrationDataStore(private val context: Context) {
    // Obtener flujo del contador
    val waterCountFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[HydrationPreferences.WATER_COUNT] ?: 0 }

    // Guardar el contador
    suspend fun saveWaterCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[HydrationPreferences.WATER_COUNT] = count
        }
    }
}
