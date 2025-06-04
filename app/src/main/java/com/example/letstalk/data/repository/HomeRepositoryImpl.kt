package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.utils.AuthUiState
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth
) : HomeService {

    override fun getAllUsers(): Flow<Resource<List<User>>> {
        return callbackFlow {

            val listener = firebaseStore.collection("Users")
                .addSnapshotListener { snap, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.localizedMessage ?: "Something went wrong"))
                    }
                    if (snap != null) {
                        val userList = snap.documents.mapNotNull {
                            it.toObject(User::class.java)
                        }.toList()
                        trySend(Resource.Success(userList))
                    }
                }
            awaitClose {
                println("removing listener")
                listener.remove()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signOut(): AuthUiState {

        return suspendCancellableCoroutine { cont ->
            val userId = firebaseAuth.currentUser?.uid
            firebaseStore.collection("Users")
                .document(userId!!)
                .update("status", "offline")
                .addOnCompleteListener { task ->
                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        firebaseAuth.signOut()
                        cont.resume(AuthUiState.Success) {
                            Log.d("Error", "Coroutine get cancelled")
                        }
                    } else {
                        cont.resume(
                            AuthUiState.Error(task.exception?.localizedMessage ?: "Sign out failed try later")
                        ) {
                            Log.d("Error", "Coroutine get cancelled")
                        }
                    }
                }.addOnFailureListener {
                    if (!cont.isActive) return@addOnFailureListener
                    cont.resume(
                        AuthUiState.Error(it.localizedMessage ?: "Sign out failed try later")
                    ) {
                        Log.d("Error", "Coroutine get cancelled")
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun setUserStatus(status: String):Resource<String> {
        return suspendCancellableCoroutine { cont ->
            val userId =firebaseAuth.currentUser?.uid
            firebaseStore.collection("Users")
                .document(userId!!)
                .update("status",status)
                .addOnCompleteListener{task->
                   if(!cont.isActive) return@addOnCompleteListener
                    if(task.isSuccessful){
                        cont.resume(Resource.Success("done")){
                            Log.d("Error", "Coroutine get cancelled")
                        }
                    }else{
                            cont.resume(Resource.Error(task.exception?.localizedMessage?:"Something went wrong")){
                                Log.d("Error", "Coroutine get cancelled")
                            }

                    }

                }
        }
    }


}