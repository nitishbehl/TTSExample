package com.example.ttsexample.utils

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import android.content.ActivityNotFoundException
import android.widget.Toast

fun startSpeechToText(
    launcher: ActivityResultLauncher<Intent>,
    context: Context
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...")
    }

    try {
        launcher.launch(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
    }
}
