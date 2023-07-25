package com.example.mindbox.models

data class ImageGenerateResponse(
    val created: Int,
    val data: List<GenerateImage>
)
