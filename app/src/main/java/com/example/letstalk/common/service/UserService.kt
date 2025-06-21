package com.example.letstalk.common.service

import com.example.letstalk.common.utils.Resource
import com.example.letstalk.db.model.UserProfileEntity

interface UserService {
  suspend fun setUserStatus(status:String): Resource<String>
  suspend fun fetchUsers():Resource<List<UserProfileEntity>>
}