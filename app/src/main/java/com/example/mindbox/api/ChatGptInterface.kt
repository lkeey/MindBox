package com.example.mindbox.api

import com.example.mindbox.models.ImageGenerateRequest
import com.example.mindbox.models.ImageGenerateResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptInterface {

    @POST("/v1/images/generations")
    suspend fun generateImage(
        @Header("Content-Type") contentType: String,
        @Header("Authorization") authorization: String,
        @Body request: RequestBody
    ) : ImageGenerateResponse

}