package com.example.letstalk.common.service

import com.example.letstalk.utils.Resource

interface UserService {
  suspend fun setUserStatus(status:String):Resource<String>
}