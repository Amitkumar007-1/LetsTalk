package com.example.letstalk.data.model

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("secure_url")
    val imageUrl:String="",
    @SerializedName("public_id")
    val publicId:String=""
)