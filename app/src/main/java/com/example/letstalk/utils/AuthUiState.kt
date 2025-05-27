package com.example.letstalk.utils

sealed class AuthUiState {
     data object Success:AuthUiState()
    data class Error(val error:String?=null):AuthUiState()
     data object Loading : AuthUiState()
     data object Nothing : AuthUiState()
}