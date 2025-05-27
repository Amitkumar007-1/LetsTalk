package com.example.letstalk.presentation.screens.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(application: Application,private val homeService: HomeService) :AndroidViewModel(application) {


     val userUiState=   homeService.getAllUsers()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5000)
                ,Resource.Loading)

}