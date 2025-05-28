package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.domain.service.SignInService
import com.example.letstalk.utils.AuthUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor() : SignInService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signIn(email: String, password: String): AuthUiState {
        return suspendCancellableCoroutine { cont ->
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        cont.resume(AuthUiState.Success) {
                            Log.d("AuthRepo", "Coroutine Get Cancelled")
                        }
                    } else {
                       val msg= task.exception?.message?:"Something went wrong"

                        cont.resume(AuthUiState.Error(if(msg.contains("auth credential", ignoreCase = true))
                            "Invalid email or password" else msg)) {
                            Log.d("AuthRepo", "Coroutine Get Cancelled")
                        }
                    }

                }.addOnFailureListener { fail->
                    if(!cont.isActive) return@addOnFailureListener
                    cont.resume(AuthUiState.Error(fail.localizedMessage)){
                        Log.d("AuthRepo", "Coroutine Get Cancelled")
                    }

                }
        }
    }
}