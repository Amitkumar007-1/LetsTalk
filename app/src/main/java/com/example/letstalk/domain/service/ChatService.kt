package com.example.letstalk.domain.service

import com.example.letstalk.data.model.Message
import com.example.letstalk.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatService {
    suspend fun sendMessage(receiverId:String,message:String):Resource<String>
    fun getAllMessages(friendId:String): Flow<Resource<List<Message>>>
}