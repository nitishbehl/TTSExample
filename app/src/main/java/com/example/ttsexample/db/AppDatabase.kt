package com.example.ttsexample.db



import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SpeechEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun speechDao(): SpeechDao
}
