package com.example.letstalk.data.repository

import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseStore: FirebaseFirestore
    ,private val firebaseAuth:FirebaseAuth):HomeService {

    override fun getAllUsers():Flow<Resource<List<User>>>{
        return callbackFlow {

            val listener= firebaseStore.collection("Users")
                .addSnapshotListener{snap,error->
                    if(error!=null){
                        trySend(Resource.Error(error.localizedMessage?:"Something went wrong"))
                    }
                    if(snap!=null){
                       val userList= snap.documents.mapNotNull {
                           it.toObject(User::class.java)
                       }
                           .filter {!it.userid.equals(firebaseAuth.currentUser!!.uid)}
                            .toList()
                        trySend(Resource.Success(userList))
                    }
                }
             awaitClose{
                 println("removing listener")
                 listener.remove()
             }
        }
    }
}