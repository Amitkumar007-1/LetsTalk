package com.example.letstalk.presentation.screens.sign_in.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.letstalk.domain.service.SignInService
import com.example.letstalk.utils.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(application: Application,private val signInService:SignInService):
    AndroidViewModel(application) {
        private val _authUiState= MutableSharedFlow<AuthUiState>()
        val authUiState get()=_authUiState.asSharedFlow()

         fun signInUser(email:String,password :String){
           viewModelScope.launch {
               _authUiState.emit( signInService.signIn(email,password))
           }
        }
}