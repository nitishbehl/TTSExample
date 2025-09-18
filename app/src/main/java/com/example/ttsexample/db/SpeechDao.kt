package com.example.ttsexample.db



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface SpeechDao {

    @Insert
    suspend fun insertSpeech(speech: SpeechEntity)

    @Query("SELECT * FROM SpeechHistory ORDER BY timestamp DESC")
    suspend fun getAllHistory(): List<SpeechEntity>

    @Query("DELETE FROM SpeechHistory WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM SpeechHistory")
    suspend fun clearHistory()
}
