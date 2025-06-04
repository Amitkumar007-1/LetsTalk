package com.example.letstalk.domain.service

import com.example.letstalk.data.model.User
import com.example.letstalk.utils.AuthUiState
import com.example.letstalk.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HomeService {
     fun getAllUsers(): Flow<Resource<List<User>>>
     suspend fun signOut():AuthUiState
     suspend fun setUserStatus(status:String):Resource<String>
}