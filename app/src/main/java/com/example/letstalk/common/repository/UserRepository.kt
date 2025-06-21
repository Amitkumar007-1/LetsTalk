package com.example.letstalk.common.repository

import android.util.Log
import com.example.letstalk.common.service.UserService
import com.example.letstalk.common.utils.Resource
import com.example.letstalk.data.model.User
import com.example.letstalk.db.model.UserProfileEntity
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
                        cont.resume(Resource.Success("done")) {}
                    } else {
                        cont.resume(
                            Resource.Error(
                                task.exception?.localizedMessage ?: "Something went wrong"
                            )
                        ) {}

                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchUsers():Resource<List<UserProfileEntity>> {
        return suspendCancellableCoroutine {cont->
            firebaseStore.collection("Users")
                .get()
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        val userProfileList= task.result.documents.mapNotNull {
                            val userObj= it.toObject(User::class.java)
                            userObj?.let {user->
                                UserProfileEntity(user.userid, user.username, user.imageData.imageUrl)
                            }
                        }.toList()
                        cont.resume(Resource.Success(userProfileList)){}
                    }else{
                        cont.resume(Resource.Error(task.exception?.message?:"Something went wrong")){}
                    }
                }

        }
    }
}