package com.example.letstalk.domain.service

import com.example.letstalk.common.utils.AuthUiState

interface SignInService {
    suspend fun signIn(email: String ,password:String): AuthUiState
}