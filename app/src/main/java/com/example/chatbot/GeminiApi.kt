package com.example.chatbot

import retrofit2.http.*;

interface GeminiApi {
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun getResponse(
        @Header("Authorization") authHeader: String,
        @Body body: GeminiRequestBody
    ): GeminiResponse
}

data class GeminiRequestBody(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)
data class GeminiResponse(val generated: List<Generated>)
data class Generated(val part: Part)