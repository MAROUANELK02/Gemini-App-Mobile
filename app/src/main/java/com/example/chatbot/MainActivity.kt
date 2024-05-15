package com.example.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatbot.ui.theme.ChatBotTheme
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatBotTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChatScreen()
                }
            }
        }
    }
}

@Composable
fun ChatScreen() {
    var message by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var exceptionMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Enter your message") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            coroutineScope.launch {
                try {
                    val apiKey = getApiKey()
                    response = getResponseFromGemini(message)
                } catch (e: Exception) {
                    exceptionMessage = e.message ?: "Unknown error"
                }
            }
        }) {
            Text("Send")
        }
        Text(text = response)
        if (exceptionMessage.isNotEmpty()) {
            Text(text = "Error: $exceptionMessage")
        }
    }
}

suspend fun getApiKey(): String {
    return "AIzaSyAAwv5Nr5Pwb2LKFIoMRNFVTeH9qeNkOkU"
}

suspend fun getResponseFromGemini(message: String): String {
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = getApiKey()
    )

    var retryCount = 0
    var response: String? = null
    while (response == null && retryCount < MAX_RETRIES) {
        try {
            val result = generativeModel.generateContent(message)
            response = result.text
        } catch (e: Exception) {
            // Handle rate limit error (HTTP 429)
            if (e is RateLimitException) {
                delay((2.toDouble().pow(retryCount) * 1000).toLong())
                retryCount++
            } else {
                throw e
            }
        }
    }

    return response ?: "No response received after $MAX_RETRIES retries."
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatBotTheme {
        ChatScreen()
    }
}

class RateLimitException(message: String) : Exception(message)

const val MAX_RETRIES = 3  // Adjust as needed
