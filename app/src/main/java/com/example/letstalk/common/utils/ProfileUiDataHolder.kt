package com.example.letstalk.common.utils

import com.example.letstalk.data.model.User
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

data class ProfileUiDataHolder(
    val profileState:StateFlow<User>,
    val profileCrudState:SharedFlow<String>
)
