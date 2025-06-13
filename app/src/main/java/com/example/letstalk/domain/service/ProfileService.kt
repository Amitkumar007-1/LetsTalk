package com.example.letstalk.domain.service

import com.example.letstalk.data.model.ImageData
import com.example.letstalk.data.model.User
import com.example.letstalk.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileService {
    suspend fun uploadProfileToCloudinary(
        file: MultipartBody.Part,
        presetRequest: RequestBody
    ): Flow<Resource<ImageData>>

    suspend fun uploadProfilePicToFireBase(imageData: ImageData): Resource<String>
    suspend fun deleteProfileCloudinary(
        publicId: String,
        apiKey: String,
        timeStamp: String,
        signature: String
    ): Flow<Resource<String>>

    fun getUserDetails(userId: String): Flow<Resource<User>>
    suspend fun deleteProfilePicFirebase(): Resource<String>

//    suspend fun updateProfilePicToCloudinary(
//        file: MultipartBody.Part,
//        presetRequest: RequestBody,
//        publicId: RequestBody,
//        overwriteRequest: RequestBody
//    ): Flow<Resource<ImageData>>
}