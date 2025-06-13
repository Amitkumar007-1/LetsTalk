package com.example.letstalk.presentation.screens.sign_up.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.letstalk.data.model.ImageData
import com.example.letstalk.domain.service.SignUpService
import com.example.letstalk.utils.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(application: Application,private val signUpService: SignUpService):AndroidViewModel(application) {
    private  val _signUpUiState = MutableSharedFlow<AuthUiState>()
    val signUpUiState get() = _signUpUiState.asSharedFlow()

    fun signUp(email:String,password:String,name:String){
        viewModelScope.launch {
            _signUpUiState.emit(AuthUiState.Loading)
            _signUpUiState.emit(signUpService.signUp(name,email,password,"online", ImageData()))
        }
    }
}