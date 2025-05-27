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
            cont.resume(AuthUiState.Loading){
                Log.d("AuthRepo", "Coroutine Get Cancelled")
            }
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(AuthUiState.Success) {
                            Log.d("AuthRepo", "Coroutine Get Cancelled")
                        }
                    } else {
                        cont.resume(AuthUiState.Error(task.exception?.message)) {
                            Log.d("AuthRepo", "Coroutine Get Cancelled")
                        }
                    }

                }
        }
    }
}