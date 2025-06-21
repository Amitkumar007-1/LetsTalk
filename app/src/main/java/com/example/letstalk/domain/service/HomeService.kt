package com.example.letstalk.domain.service

import com.example.letstalk.data.model.RecentChats
import com.example.letstalk.data.model.User
import com.example.letstalk.common.utils.AuthUiState
import com.example.letstalk.common.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HomeService {
     fun getAllUsers(): Flow<Resource<List<User>>>
     suspend fun signOut(): AuthUiState
     suspend fun setUserStatus(status:String): Resource<String>
     fun getRecentChats():Flow<Resource<List<RecentChats>>>
}