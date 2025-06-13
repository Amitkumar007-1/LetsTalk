package com.example.letstalk.utils

import com.example.letstalk.data.model.User
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiDataHolder(
    val profileState:StateFlow<User>
)
