package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.domain.service.SignUpService
import com.example.letstalk.utils.AuthUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor() : SignUpService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        status: String,
        imageUrl: String
    ): AuthUiState {
        return suspendCancellableCoroutine { cont ->

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {

                        task.result.user?.apply {
                            updateProfile(
                                UserProfileChangeRequest
                                    .Builder()
                                    .setDisplayName(name)
                                    .build()
                            ).addOnSuccessListener {
                                if (cont.isActive) {
                                    println("success")
                                    cont.resume(AuthUiState.Success) {
                                        Log.d("SignUpAuth", "Coroutine get cancelled")
                                    }
                                }
                            }.addOnFailureListener {
                                if (cont.isActive) {
                                    println("fail")
                                    cont.resume(AuthUiState.Error("Something went wrong user not created")) {}
                                }
                            }
                        } ?: run{

                            if(cont.isActive){
                                println("runnnn")
                                cont.resume(AuthUiState.Error("Something went wrong user not created")) {}
                            }
                        }


                    } else {
                        if(!cont.isActive) return@addOnCompleteListener
                        println("elsee")
                        cont.resume(AuthUiState.Error(task.exception?.localizedMessage)) {}
                    }
                }.addOnFailureListener { err ->
                    if(!cont.isActive) return@addOnFailureListener
                    println("failleeee")
                    cont.resume(AuthUiState.Error(err.localizedMessage)) {}
                }
        }
    }
}