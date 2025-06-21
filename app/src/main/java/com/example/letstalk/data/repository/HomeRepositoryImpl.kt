package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.data.model.RecentChats
import com.example.letstalk.data.model.User
import com.example.letstalk.db.dao.UserProfileDao
import com.example.letstalk.db.model.UserProfileEntity
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.common.utils.AuthUiState
import com.example.letstalk.common.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth,private val userProfileDao: UserProfileDao
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
    override suspend fun setUserStatus(status: String): Resource<String> {
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

   override fun getRecentChats():Flow<Resource<List<RecentChats>>>{
      return  callbackFlow {
            val userId =firebaseAuth.currentUser?.uid
            val listener= firebaseStore.collection("Users")
                .document(userId!!)
                .collection("Recent Chats")
                .orderBy("timeStamp",Query.Direction.DESCENDING)
                .addSnapshotListener{snap,err->
                    if(err!=null){
                        trySend(Resource.Error(err.message?:"Something went wrong"))
                        return@addSnapshotListener
                    }
                   if(snap!=null){
                     val recentChatsList = snap.documents.mapNotNull {
                           it.toObject(RecentChats::class.java)?.apply { setTime() }
                       }.toList()

                       launch {
                         val updatedRecentChats=  fetchCacheProfile(recentChatsList)
                           updatedRecentChats?.let {
                               trySend(Resource.Success(it))
                           }
                       }
                   }
                }
            awaitClose{
                listener.remove()
            }
        }
    }

   private suspend fun fetchCacheProfile(recentChats: List<RecentChats>):List<RecentChats>?{
     return   recentChats.takeIf { it.isNotEmpty() }
           ?.let {

               val friendIds= mutableListOf<String>()

               val recentChatMap= recentChats.map {
                   friendIds.add(it.chatWith)
                   it
               }.associateBy{ it.chatWith }

               val missingIds=friendIds.filter { userProfileDao.getUserProfileOnId(it)==null }

               if(missingIds.isNotEmpty()){
                  val userProfileEntity= getProfileFromFirebase(missingIds)
                   userProfileDao.insertUserProfile(userProfileEntity)
                 val daoList = userProfileDao.getAllUserProfile()
                   daoList.map { entity->
                      recentChatMap[entity.uid]?.let { chat->
                          chat.name=entity.name.replaceFirstChar {char-> char.uppercase() }
                          chat.imageUrl=entity.imageUrl
                      }

                   }
                   recentChatMap.values.toList()
               }
               else{
                   val userProfileData=userProfileDao.getAllUserProfile()
                   userProfileData.map {
                       val chat= recentChatMap[it.uid]
                       chat?.name=it.name.replaceFirstChar {char-> char.uppercase() }
                       chat?.imageUrl=it.imageUrl
                   }
                   return recentChatMap.values.toList()
               }
           }
    }

    private suspend fun getProfileFromFirebase(missingIds:List<String>):List<UserProfileEntity>{
      return missingIds.chunked(5).flatMap {batch->
           val snapshot=firebaseStore.collection("Users")
               .whereIn(FieldPath.documentId(),batch)
               .get().await()

           snapshot.documents.mapNotNull {
               val userObj= it.toObject(User::class.java)
               userObj?.let {user->
                   UserProfileEntity(user.userid,user.username,user.imageData.imageUrl)
               }
           }.toList()
       }
    }

}