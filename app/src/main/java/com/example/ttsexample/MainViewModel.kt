package com.example.ttsexample


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ttsexample.db.AppDatabase
import com.example.ttsexample.db.SpeechEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val db: AppDatabase) : ViewModel() {

    // This holds the speech history
    val speechListState: MutableState<List<SpeechEntity>> = mutableStateOf(emptyList())

    init {
        refreshList()
    }

    private fun refreshList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = db.speechDao().getAllHistory()
            withContext(Dispatchers.Main) {
                speechListState.value = list
            }
        }
    }

    fun addSpeech(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.speechDao().insertSpeech(
                SpeechEntity(text = text, timestamp = System.currentTimeMillis())
            )
            refreshList()
        }
    }

    fun deleteSpeech(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.speechDao().deleteById(id)
            refreshList()
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            db.speechDao().clearHistory()
            refreshList()
        }
    }


}
