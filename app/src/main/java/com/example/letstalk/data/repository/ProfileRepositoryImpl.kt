package com.example.letstalk.data.repository

import android.util.Log
import com.example.letstalk.data.model.ImageData
import com.example.letstalk.data.model.User
import com.example.letstalk.data.network.ProfileApiService
import com.example.letstalk.domain.service.ProfileService
import com.example.letstalk.common.utils.Cloudinary
import com.example.letstalk.common.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val firebaseStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :
    ProfileService {
    override suspend fun uploadProfileToCloudinary(file: MultipartBody.Part, presetRequest: RequestBody): Flow<Resource<ImageData>> =
        flow {
            emit(Resource.Loading)
            val response = profileApiService.uploadProfilePic(
                cloudName = Cloudinary.CLOUD_NAME,
                file = file,
                preset = presetRequest
            )
            if(response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()))
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun uploadProfilePicToFireBase(imageData: ImageData): Resource<String> {
        val userId=firebaseAuth.currentUser?.uid
        return suspendCancellableCoroutine {cont->
            firebaseStore.collection("Users")
                .document(userId!!)
                .update(mapOf("imageData.imageUrl" to imageData.imageUrl,"imageData.publicId" to imageData.publicId))
                .addOnCompleteListener{task->
                    if(!cont.isActive) return@addOnCompleteListener
                    if(task.isSuccessful){
                        cont.resume(Resource.Success("Uploaded Successfully")){
                            Log.d("Error","Coroutine get cancelled")
                        }
                    }else{
                        cont.resume(Resource.Error(task.exception?.message?:"Something went wrong")){
                            Log.d("Error","Coroutine get cancelled")
                        }
                    }
                }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteProfilePicFirebase(): Resource<String> {
        val userId=firebaseAuth.currentUser?.uid
        return suspendCancellableCoroutine {cont->
            firebaseStore.collection("Users")
                .document(userId!!)
                .update(mapOf("imageData.imageUrl" to "","imageData.publicId" to ""))
                .addOnCompleteListener{task->
                    if(!cont.isActive) return@addOnCompleteListener
                    if(task.isSuccessful){
                        cont.resume(Resource.Success("Deleted Successfully")){
                            Log.d("Error","Coroutine get cancelled")
                        }
                    }else{
                        cont.resume(Resource.Error(task.exception?.message?:"Something went wrong")){
                            Log.d("Error","Coroutine get cancelled")
                        }
                    }
                }
        }
    }

    override suspend fun deleteProfileCloudinary(
        publicId: String,
        apiKey: String,
        timeStamp: String,
        signature: String
    ) :Flow<Resource<String>>{
       return flow {
           emit(Resource.Loading)
           val result = profileApiService.deletePic(
               Cloudinary.CLOUD_NAME,
               publicId,
               apiKey,
               timeStamp,
               signature
           )

           if (result.isSuccessful) {
               Log.d("Delete", "Successfully")
               emit(Resource.Success("Done"))
           } else {
               Log.d("Delete", "Fail")
               emit(Resource.Error(result.message()))           }
       }
    }

    override fun getUserDetails(userId: String): Flow<Resource<User>> {
        return callbackFlow {

            val listener = firebaseStore.collection("Users")
                .document(userId)
                .addSnapshotListener { snap, error ->
                    if (snap != null && snap.exists()) {
                        snap.toObject(User::class.java)?.let {
                            trySend(Resource.Success(it))
                        }
                    } else {
                        trySend(Resource.Error(error?.message))
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }
}