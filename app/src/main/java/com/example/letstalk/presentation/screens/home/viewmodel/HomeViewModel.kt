package com.example.letstalk.presentation.screens.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.common.utils.AuthUiState
import com.example.letstalk.common.utils.HomeUiDataState
import com.example.letstalk.common.utils.LoadErrorUiState
import com.example.letstalk.common.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val homeService: HomeService,
    private val fireBaseAuth: FirebaseAuth
) : AndroidViewModel(application) {
    private val _loadErrorUiState = MutableStateFlow(LoadErrorUiState(loading = true, error = null))
    val loadErrorUiState get() = _loadErrorUiState.asStateFlow()
    private val _profileState = MutableSharedFlow<User>(replay = 1)

    private val _signOutState = MutableStateFlow("")
    private val _userListState = homeService.getAllUsers()
        .transform { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _loadErrorUiState.update { it.copy(loading = true, error = null) }
                }

                is Resource.Error -> {
                    _loadErrorUiState.update { it.copy(loading = false, error = resource.message) }
                }

                is Resource.Success -> {
                    _loadErrorUiState.update { it.copy(loading = false, error = null) }
                    emit(resource.data)
                }
            }
        }
        .map {
            it.map { user ->
                if (user.userid.equals(fireBaseAuth.currentUser?.uid)) {
                    _profileState.emit(user)
                }
                user
            }.filter { user -> !user.userid.equals(fireBaseAuth.currentUser?.uid) }
                .toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList()
        )
    private val _recentChats = homeService.getRecentChats()
        .transform { resource ->
            when (resource) {
                is Resource.Success -> {
                    emit(resource.data)
                }

                is Resource.Error -> {
                    throw Exception(resource.message ?: "Something went wrong")
                }

                else -> Unit
            }
        }.catch { err ->
            _loadErrorUiState.update { it.copy(loading = false, error = err.message) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), emptyList())

    val homeUiStateHolder =
        HomeUiDataState(
            userListState = _userListState,
            signOutState = _signOutState,
            userProfileState = _profileState,
            recentChatsState = _recentChats
        )

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            //loading
            _loadErrorUiState.update { it.copy(loading = true, error = null) }

            when (val state = homeService.signOut()) {
                is AuthUiState.Error -> {
                    _loadErrorUiState.update { it.copy(loading = false, error = state.error) }
                }

                is AuthUiState.Success -> {
                    _loadErrorUiState.update { it.copy(loading = false, error = null) }
                    _signOutState.emit("Sign out successfully")
                }

                else -> Unit
            }
        }
    }


    fun setUserStatus(status: String) {
        viewModelScope.launch {
            homeService.setUserStatus(status)

        }
    }
}