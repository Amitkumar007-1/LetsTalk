package com.example.letstalk.utils

import com.example.letstalk.data.model.User
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


data class HomeUiDataState(
    val userListState:StateFlow<List<User>>,
    val signOutState:StateFlow<String>,
    val userProfileState:SharedFlow<User>
)