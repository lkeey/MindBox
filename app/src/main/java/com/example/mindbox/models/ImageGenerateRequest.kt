package com.example.mindbox.models

data class ImageGenerateRequest(
    val prompt: String,
    val n: Int,
    val size: String
)
