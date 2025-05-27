package com.example.letstalk.domain.service

import com.example.letstalk.utils.AuthUiState

interface SignInService {
    suspend fun signIn(email: String ,password:String): AuthUiState
}