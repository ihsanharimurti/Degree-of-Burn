package com.example.degreeofburn.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.degreeofburn.data.converter.StringListConverter

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val weight: String,
    val height: String,
    val age: String,
    val bloodType: String,
    @TypeConverters(StringListConverter::class)
    val selectedBodyParts: List<String>,
    val timestamp: Long = System.currentTimeMillis(),
    val syncedToRemote: Boolean = false
)