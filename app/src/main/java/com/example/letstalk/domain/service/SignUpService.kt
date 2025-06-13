package com.example.letstalk.domain.service

import com.example.letstalk.data.model.ImageData
import com.example.letstalk.utils.AuthUiState

interface SignUpService {
    suspend fun signUp(name:String,email:String,password:String,status:String,imageData:ImageData): AuthUiState
}