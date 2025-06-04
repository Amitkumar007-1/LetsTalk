package com.example.letstalk.common.repository

import android.util.Log
import com.example.letstalk.common.service.UserService
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStore: FirebaseFirestore
) : UserService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun setUserStatus(status: String): Resource<String> {
        return suspendCancellableCoroutine { cont ->
            val userId = firebaseAuth.currentUser?.uid
            firebaseStore.collection("Users")
                .document(userId!!)
                .update("status", status)
                .addOnCompleteListener { task ->
                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        cont.resume(Resource.Success("done")) {
                            Log.d("Error", "Coroutine get cancelled")
                        }
                    } else {
                        cont.resume(
                            Resource.Error(
                                task.exception?.localizedMessage ?: "Something went wrong"
                            )
                        ) {
                            Log.d("Error", "Coroutine get cancelled")
                        }

                    }
                }
        }
    }
}