package com.example.core

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object WaterCountRepository {
    private const val WATER_COUNT_PATH = "usuarios/unico/waterCount"

    // Observa los cambios en el contador en tiempo real
    fun observeWaterCount(onChanged: (Int) -> Unit): ValueEventListener {
        val ref = FirebaseDatabase.getInstance().getReference(WATER_COUNT_PATH)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Int::class.java) ?: 0
                onChanged(value)
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        ref.addValueEventListener(listener)
        return listener
    }

    // Cambia el contador en la base de datos
    fun setWaterCount(count: Int) {
        val ref = FirebaseDatabase.getInstance().getReference(WATER_COUNT_PATH)
        ref.setValue(count)
    }

    // Quita el listener si es necesario (opcional)
    fun removeListener(listener: ValueEventListener) {
        val ref = FirebaseDatabase.getInstance().getReference(WATER_COUNT_PATH)
        ref.removeEventListener(listener)
    }
}
