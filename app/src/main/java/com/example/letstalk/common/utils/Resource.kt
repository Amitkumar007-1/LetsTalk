package com.example.letstalk.common.utils

sealed class Resource<out T> {
    class Success<T>(val data:T): Resource<T>()
    class Error(val message:String?): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}