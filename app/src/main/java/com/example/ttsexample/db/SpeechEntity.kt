package com.example.ttsexample.db



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SpeechHistory")
data class SpeechEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val timestamp: Long
)
