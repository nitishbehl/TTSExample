package com.example.ttsexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import java.util.*

@Composable
fun SpeechScreen(
    tts: TextToSpeech,
    activity: ComponentActivity,
    historyList: MutableList<String>,
    navController: NavHostController
) {
    //Stores the text converted from your speech.
    var recognizedText by remember { mutableStateOf("Your speech will appear here") }
    //Checks if Text-to-Speech (TTS) is ready for the language (US English).
    var ttsReady by remember { mutableStateOf(tts.isLanguageAvailable(Locale.US) >= 0) }
    val context = LocalContext.current

    //This handles the speech recognition result after the user speaks.
    //ActivityResultLauncher is how Jetpack Compose asks Android to do something and then return a result.
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val textResult = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            recognizedText = textResult?.get(0) ?: "No speech recognized"
        } else {
            recognizedText = "Speech recognition failed"
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = recognizedText,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ExtendedFloatingActionButton(
                text = { Text("Record") },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Mic") },
                onClick = {
                    if (checkMicPermission(activity)) {
                        startSpeechToText(speechLauncher, context)
                    } else {
                        Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            ExtendedFloatingActionButton(
                text = { Text("Speak") },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "TTS") },
                onClick = {
                    if (recognizedText.isNotEmpty() && ttsReady) {
                        tts.speak(recognizedText, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
                        historyList.add(0, recognizedText)
                    } else {
                        Toast.makeText(context, "TTS not ready or text empty", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("history_screen") },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("View History")
        }
    }
}

// Prepares an Intent to ask Android for speech input.
//Launches the speech recognizer.
//Handles cases when the device doesn’t support speech recognition.
private fun startSpeechToText(
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    context: Context
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
    }

    val pm = context.packageManager
    val activities = pm.queryIntentActivities(intent, 0)
    if (activities.isNotEmpty()) {
        try {
            launcher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to launch speech recognition", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
    }
}

//Checks if the app has permission to use the microphone.
//If not, requests permission from the user.
private fun checkMicPermission(activity: ComponentActivity): Boolean {
    return if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        false
    } else true
}
