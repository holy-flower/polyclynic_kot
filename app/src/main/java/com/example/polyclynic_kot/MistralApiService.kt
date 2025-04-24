package com.example.polyclynic_kot

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MistralApiService {
    @POST("/v1/chat/completions")
    fun completeChat(@Body requestBody: RequestBody): Call<MedicationResponse>
}

data class MedicationResponse(
    val choices: List<Choice>
)

// Вариант выбора — одно сообщение
data class Choice(
    val message: Message
)

// Сообщение (вопрос или ответ)
data class Message(
    val role: String,
    val content: String
)

// Запрос на получение данных о лекарстве
data class MedicationRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double
)