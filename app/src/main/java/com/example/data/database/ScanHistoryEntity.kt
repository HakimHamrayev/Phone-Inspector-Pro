package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String,
    val model: String,
    val androidVersion: String,
    val healthScore: Int,
    val batteryHealth: String,
    val rootStatus: String,
    val cpuModel: String,
    val ramInfo: String,
    val storageInfo: String,
    val displayInfo: String,
    val summaryNotes: String
)
