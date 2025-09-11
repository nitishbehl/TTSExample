package com.example.ttsexample

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
            }
        }

        setContent {
            val navController = rememberNavController()
            val historyList = remember { mutableStateListOf<String>() }

            // Track selected bottom nav item
            var selectedItem by remember { mutableStateOf("speech_screen") }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        NavigationBarItem(
                            selected = selectedItem == "speech_screen",
                            onClick = {
                                selectedItem = "speech_screen"
                                navController.navigate("speech_screen") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Speech") },
                            label = { Text("Speech") }
                        )
                        NavigationBarItem(
                            selected = selectedItem == "history_screen",
                            onClick = {
                                selectedItem = "history_screen"
                                navController.navigate("history_screen") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "History"
                                )
                            },
                            label = { Text("History") }
                        )
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavHost(navController = navController, startDestination = "speech_screen") {
                        composable("speech_screen") {
                            SpeechScreen(tts, this@MainActivity, historyList, navController)
                        }
                        composable("history_screen") {
                            HistoryScreen(historyList, navController)
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
