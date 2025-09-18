package com.example.ttsexample

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.ttsexample.db.AppDatabase
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "speech-history.db"
        ).fallbackToDestructiveMigration().build()

        val viewModel = MainViewModel(db)

        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
            }
        }

        setContent {
            val navController = rememberNavController()
            var selectedItem by remember { mutableStateOf("speech_screen") }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        val items = listOf(
                            "speech_screen" to Icons.Default.PlayArrow,
                            "history_screen" to Icons.Default.CheckCircle
                        )

                        items.forEach { (route, icon) ->
                            NavigationBarItem(
                                selected = selectedItem == route,
                                onClick = {
                                    selectedItem = route
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = route) },
                                label = { Text(if (route == "speech_screen") "Speech" else "History") }
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavHost(navController = navController, startDestination = "speech_screen") {
                        composable("speech_screen") {
                            SpeechScreen(tts, this@MainActivity, viewModel, navController)
                        }
                        composable("history_screen") {
                            HistoryScreen(viewModel, navController)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
