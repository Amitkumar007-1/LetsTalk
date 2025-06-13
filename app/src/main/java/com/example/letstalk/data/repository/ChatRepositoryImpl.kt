package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.data.model.Message
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.ChatService
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val firebaseStore:FirebaseFirestore,private val firebaseAuth:FirebaseAuth):ChatService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun sendMessage(receiverId: String, message: String):Resource<String> {
        return suspendCancellableCoroutine { cont->
            val senderId=firebaseAuth.currentUser!!.uid
            val messageMap=createMessageMap(senderId,receiverId,message)
            val chatRoomId=listOf(senderId,receiverId).sorted().joinToString("")
            firebaseStore.collection("Messages")
                .document(chatRoomId)
                .collection("chats")
                .add(messageMap)
                .addOnSuccessListener{_->
                    if(!cont.isActive)
                        return@addOnSuccessListener
                    println("success ho rha ")
                    cont.resume(Resource.Success("Message Sent")){
                        Log.d("Error","Coroutine get cancelled")
                    }
                }.addOnFailureListener {fail->
                    if(!cont.isActive) return@addOnFailureListener
                    cont.resume(Resource.Error(fail.message)){
                        Log.d("Error","Coroutine get cancelled")
                    }
                }

        }
    }


    override fun getAllMessages(friendId: String):Flow<Resource<List<Message>>> {
       return callbackFlow {
           trySend(Resource.Loading)
            val senderId=firebaseAuth.currentUser!!.uid
            val chatRoomId= listOf(friendId,senderId).sorted().joinToString("")
           val listener= firebaseStore.collection("Messages").document(chatRoomId)
               .collection("chats")
               .orderBy("timeStamp",Query.Direction.DESCENDING)
               .addSnapshotListener{snap,err->
                   if(err!=null){
                       trySend(Resource.Error(err.localizedMessage ?:"Something went wrong"))
                   }
                   if(snap!=null){
                      val messageList= snap.documents.mapNotNull {
                          it.toObject(Message::class.java)?.apply {setDataTime() }
                       }.toList()
                       trySend(Resource.Success(messageList))
                   }
               }
            awaitClose{
                listener.remove()
            }

        }
    }

    override fun getFriendDetail(friendId: String) = callbackFlow {
        val listener= firebaseStore.collection("Users")
            .document(friendId)
            .addSnapshotListener{snap,err->
                if(snap!=null && snap.exists()){
                    snap.toObject(User::class.java)?.let {
                        println("seeeee")
                        trySend(Resource.Success(it))
                    }

                }else{
                    println("errrrr")
                    trySend(Resource.Error(err?.message))
                }
            }
        awaitClose{
            println("remoooovvvving")
            listener.remove()
        }
    }

    private fun createMessageMap(senderId:String, receiverId:String, message:String): HashMap<String, Any> {
        return hashMapOf("senderId" to senderId,"receiverId" to receiverId,"message" to message,"timeStamp" to FieldValue.serverTimestamp())
    }
}