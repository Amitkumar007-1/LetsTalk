package com.example.letstalk.presentation.screens.chats.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.savedState
import com.example.letstalk.data.model.Message
import com.example.letstalk.domain.service.ChatService
import com.example.letstalk.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val chatService: ChatService

) : AndroidViewModel(application) {
    private val _sentMsgUiState = MutableSharedFlow<Resource<String>>()
    val sentMsgUiState get() = _sentMsgUiState.asSharedFlow()

    private val friendId by lazy { savedStateHandle.get<String>("friendId") }
    lateinit var chatMessages:StateFlow<Resource<List<Message>>>

    init {
        getAllMessages()
    }

    private fun getAllMessages() {
       friendId?.let {
           chatMessages=chatService.getAllMessages(it)
               .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000),Resource.Loading)
       }
    }

    fun sendMessage( message: String) {
        viewModelScope.launch {
            friendId?.let {
                _sentMsgUiState.emit(
                    chatService.sendMessage(
                        it,
                        message
                    ))
            }?:_sentMsgUiState.emit(Resource.Error("Friend id not present"))
        }
    }


}