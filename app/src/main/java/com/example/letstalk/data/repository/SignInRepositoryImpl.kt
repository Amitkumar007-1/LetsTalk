package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.domain.service.SignInService
import com.example.letstalk.common.utils.AuthUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SignInService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signIn(email: String, password: String): AuthUiState {
        return suspendCancellableCoroutine { cont ->
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        firestore.collection("Users")
                            .document(firebaseAuth.currentUser?.uid!!)
                            .set(hashMapOf("status" to "online"), SetOptions.merge())
                            .addOnCompleteListener{task2->
                                if(task2.isSuccessful){
                                    cont.resume(AuthUiState.Success) {
                                        Log.d("AuthRepo", "Coroutine Get Cancelled")
                                    }
                                }else{
                                    cont.resume(AuthUiState.Error("Status not updated")){
                                        Log.d("AuthRepo", "Coroutine Get Cancelled")
                                    }
                                }
                            }
                    } else {
                        if(!cont.isActive) return@addOnCompleteListener
                        val msg = task.exception?.message ?: "Something went wrong"
                        cont.resume(
                            AuthUiState.Error(
                                if (msg.contains("auth credential", ignoreCase = true))
                                    "Invalid email or password" else msg
                            )
                        ) {
                            Log.d("AuthRepo", "Coroutine Get Cancelled")
                        }
                    }

                }.addOnFailureListener { fail ->
                    if (!cont.isActive) return@addOnFailureListener
                    cont.resume(AuthUiState.Error(fail.localizedMessage)) {
                        Log.d("AuthRepo", "Coroutine Get Cancelled")
                    }

                }
        }
    }
}