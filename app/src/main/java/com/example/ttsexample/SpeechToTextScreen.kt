package com.example.ttsexample

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ttsexample.utils.startSpeechToText

@Composable
fun SpeechScreen(
    tts: TextToSpeech,
    activity: ComponentActivity,
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var spokenText by remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringArrayListExtra("android.speech.extra.RESULTS")?.get(0)
            if (!resultText.isNullOrEmpty()) {
                spokenText = resultText
                tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null)
                viewModel.addSpeech(resultText) // Save to DB
            }
        }
    }

    // 🎤 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechToText(speechLauncher, context)
        } else {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speech Screen",
                style = MaterialTheme.typography.titleLarge
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (spokenText.isEmpty()) "Say something..." else spokenText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startSpeechToText(speechLauncher, context)
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Mic")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { navController.navigate("history_screen") }) {
                Text("View History")
            }
        }
    }
}
