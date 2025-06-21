package com.example.letstalk.data.repository
import com.example.letstalk.data.model.ImageData
import com.example.letstalk.domain.service.SignUpService
import com.example.letstalk.common.utils.AuthUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(private val firebaseAuth:FirebaseAuth
,private val firestore:FirebaseFirestore) : SignUpService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        status: String,
        imageData:ImageData
    ): AuthUiState {
        return suspendCancellableCoroutine { cont ->

            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                       val userId= firebaseAuth.currentUser!!.uid
                        firestore.collection("Users")
                            .document(userId)
                            .set(setUserDetail(name,email,status,imageData,userId))
                            .addOnSuccessListener {
                                if(cont.isActive){
                                    cont.resume(AuthUiState.Success) {}
                                }
                            }.addOnFailureListener{
                                if(!cont.isActive) return@addOnFailureListener
                                cont.resume(AuthUiState.Error("Something went wrong user not created")) {}
                            }

                    } else {
                        if(!cont.isActive) return@addOnCompleteListener
                        cont.resume(AuthUiState.Error(task.exception?.localizedMessage)) {}
                    }
                }.addOnFailureListener { err ->
                    if(!cont.isActive) return@addOnFailureListener
                    cont.resume(AuthUiState.Error(err.localizedMessage)) {}
                }
        }
    }
    private fun setUserDetail(name:String,email:String,status:String,imageData:ImageData,uid:String):HashMap<String,Any>{
        return hashMapOf("userid" to uid
            ,"username" to name
            ,"email" to email
            ,"status" to status
            ,"imageData" to hashMapOf("imageUrl" to imageData.imageUrl,"publicId" to imageData.publicId)
        )
    }
}