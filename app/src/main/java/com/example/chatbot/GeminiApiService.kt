package com.example.chatbot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun getResponse(authHeader: String, body: GeminiRequestBody): String {
        val response = api.getResponse(authHeader, body)
        return response.generated.firstOrNull()?.part?.text ?: "No response received"
    }
}
