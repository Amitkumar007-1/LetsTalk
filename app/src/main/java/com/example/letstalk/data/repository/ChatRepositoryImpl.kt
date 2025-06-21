package com.example.letstalk.data.repository

import com.example.letstalk.data.model.Message
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.ChatService
import com.example.letstalk.common.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ChatService {
    override suspend fun sendMessage(receiverId: String, message: String): Resource<String> {
        try {
            val senderId = firebaseAuth.currentUser!!.uid
            val messageMap = createMessageMap(senderId, receiverId, message)
            val chatRoomId = listOf(senderId, receiverId).sorted().joinToString("")

            //Setting Message
            firebaseStore.collection("Messages")
                .document(chatRoomId)
                .collection("chats")
                .add(messageMap)
                .await()

            //Setting Recent Message For LoggedIn User
            firebaseStore.collection("Users")
                .document(senderId)
                .collection("Recent Chats")
                .document(receiverId)
                .set(
                    hashMapOf(
                        "chatWith" to receiverId,
                        "lastMessage" to message,
                        "timeStamp" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                )
                .await()


            //Setting Recent Message For Receiver
            firebaseStore.collection("Users")
                .document(receiverId)
                .collection("Recent Chats")
                .document(senderId)
                .set(
                    hashMapOf(
                        "chatWith" to senderId,
                        "lastMessage" to message,
                        "timeStamp" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                )
                .await()

          return   Resource.Success("Message Sent")

        } catch (error: Exception) {
          return   Resource.Error(error.message ?: "Something went wrong")
        }
    }


    override fun getAllMessages(friendId: String): Flow<Resource<List<Message>>> {
        return callbackFlow {
            trySend(Resource.Loading)
            val senderId = firebaseAuth.currentUser!!.uid
            val chatRoomId = listOf(friendId, senderId).sorted().joinToString("")
            val listener = firebaseStore.collection("Messages").document(chatRoomId)
                .collection("chats")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        trySend(Resource.Error(err.localizedMessage ?: "Something went wrong"))
                    }
                    if (snap != null) {
                        val messageList = snap.documents.mapNotNull {
                            it.toObject(Message::class.java)?.apply { setDataTime() }
                        }.toList()
                        trySend(Resource.Success(messageList))
                    }
                }
            awaitClose {
                listener.remove()
            }

        }
    }

    override fun getFriendDetail(friendId: String) = callbackFlow {
        val listener = firebaseStore.collection("Users")
            .document(friendId)
            .addSnapshotListener { snap, err ->
                if (snap != null && snap.exists()) {
                    snap.toObject(User::class.java)?.let {
                        trySend(Resource.Success(it))
                    }

                } else {
                    trySend(Resource.Error(err?.message))
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    private fun createMessageMap(
        senderId: String,
        receiverId: String,
        message: String
    ): HashMap<String, Any> {
        return hashMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message,
            "timeStamp" to FieldValue.serverTimestamp()
        )
    }
}