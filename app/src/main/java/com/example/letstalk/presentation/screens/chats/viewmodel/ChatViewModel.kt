package com.example.letstalk.presentation.screens.chats.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.savedState
import com.example.letstalk.data.model.Message
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.ChatService
import com.example.letstalk.utils.ChatUiDataState
import com.example.letstalk.utils.LoadErrorUiState
import com.example.letstalk.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val chatService: ChatService

) : AndroidViewModel(application) {
    private val friendId by lazy { savedStateHandle.get<String>("friendId") }
    private val _loadErrorUiState= MutableStateFlow(LoadErrorUiState(loading = false, error =null))
    val loadErrorUiState get() = _loadErrorUiState.asStateFlow()
    private val _sentMsgUiState = MutableSharedFlow<String>()

    private val _chatMessages=run{
        chatService.getAllMessages(friendId?.let { friendId }?:throw Exception("Friend id null"))
            .transform {resource->
                when(resource){
                    is Resource.Loading->{
                        _loadErrorUiState.update { it.copy(loading = true, error = null) }
                    }
                    is Resource.Error->{
                        _loadErrorUiState.update { it.copy(loading = false, error = resource.message) }
                    }
                    is Resource.Success->{
                        _loadErrorUiState.update { it.copy(loading = false, error = null) }
                        emit(resource.data)
                    }
                }
            }.catch { err->
                _loadErrorUiState.update { it.copy(loading=false, error = err.message) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), emptyList())

    }
    private val _friendDetail =run{
        chatService.getFriendDetail(friendId?.let { friendId }?:throw Exception("Friend id null"))
            .transform {resourceUser->
                when(resourceUser){
                    is Resource.Success->{
                        emit(resourceUser.data)
                    }
                    is Resource.Error->{
                        _loadErrorUiState.update { it.copy(loading = false, error = resourceUser.message) }
                    }
                    else ->Unit
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000),User())
    }
    val chatUiDataHolder by lazy {
        ChatUiDataState(chatMessages = _chatMessages, sentMessage = _sentMsgUiState, friendDetail = _friendDetail)
    }


    fun sendMessage( message: String) {
        viewModelScope.launch {
            friendId?.let {id->
                 when(val result=chatService.sendMessage(id, message)){
                    is Resource.Error->{
                        _loadErrorUiState.update { it.copy(loading=false, error = result.message) }
                    }
                     is Resource.Success->{
                         _sentMsgUiState.emit(result.data)
                     }
                     else ->Unit
                }
            }?: _loadErrorUiState.update { it.copy(loading=false, error = "Friend id null") }
        }
    }


}