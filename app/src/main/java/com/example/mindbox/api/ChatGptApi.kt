package com.example.mindbox.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ChatGptApi {

    fun getApi() : ChatGptInterface {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGptInterface::class.java)
    }

}