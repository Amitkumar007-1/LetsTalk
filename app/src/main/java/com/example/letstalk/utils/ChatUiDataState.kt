package com.example.letstalk.utils

import com.example.letstalk.data.model.Message
import com.example.letstalk.data.model.User
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

data class ChatUiDataState(
    val chatMessages: StateFlow<List<Message>>,
    val sentMessage:SharedFlow<String>,
    val friendDetail:StateFlow<User>
)
