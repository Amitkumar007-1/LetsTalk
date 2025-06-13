package com.example.letstalk.presentation.screens.profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.letstalk.data.model.ImageData
import com.example.letstalk.data.model.User
import com.example.letstalk.domain.service.ProfileService
import com.example.letstalk.utils.Cloudinary
import com.example.letstalk.utils.LoadErrorUiState
import com.example.letstalk.utils.ProfileUiDataHolder
import com.example.letstalk.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val profileService: ProfileService,
    private val savedStateHandle: SavedStateHandle
) :
    AndroidViewModel(application) {
    private val userId by lazy { savedStateHandle.get<String>("userId") }

    private val _loadErrorUiState = MutableStateFlow(LoadErrorUiState(loading = true, error = null))
    val loadErrorUiState get() = _loadErrorUiState.asStateFlow()

    private val _profileCrudState = MutableStateFlow("")

    lateinit var imageData: ImageData


    private val _profileState =
        profileService.getUserDetails(userId?.let { userId } ?: throw Exception("User id null"))
            .transform { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _loadErrorUiState.update { it.copy(loading = true, error = null) }
                    }

                    is Resource.Error -> {
                        _loadErrorUiState.update {
                            it.copy(
                                loading = false,
                                error = resource.message
                            )
                        }
                    }

                    is Resource.Success -> {
                        _loadErrorUiState.update { it.copy(loading = false, error = null) }
                        emit(resource.data)
                    }
                }
            }.catch { err ->
                _loadErrorUiState.update { it.copy(loading = false, error = err.message) }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(3000),
                User()
            )
    val profileUiStateHolder = ProfileUiDataHolder(
        profileState = _profileState
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadProfilePic(file: File) {
        val (presetRequest, filePart) = preparePresetAndFilePart(file)

        viewModelScope.launch(Dispatchers.IO) {
            profileService.uploadProfileToCloudinary(filePart, presetRequest)
                .transform { response ->

                    when (response) {
                        is Resource.Loading -> {
                            _loadErrorUiState.update { it.copy(loading = true, error = null) }
                        }

                        is Resource.Error -> {
                            file.delete()
                            throw Exception(response.message ?: "Something went wrong")
                        }

                        is Resource.Success -> {
                            emit(response.data)
                        }
                    }
                }
                .flatMapConcat {
                    flow {
                        val result = profileService.uploadProfilePicToFireBase(it)
                        emit(result)
                    }
                }.catch { err ->
                    _loadErrorUiState.update { it.copy(loading = false, error = err.message) }
                }
                .collect { result ->
                    file.let {
                        if (it.exists()) {
                            it.delete()
                            Log.d("File", "File deleted successfully")
                        }
                    }
                    when (result) {
                        is Resource.Success -> _profileCrudState.emit(result.data)
                        is Resource.Error -> {
                            _loadErrorUiState.update {
                                it.copy(
                                    loading = false,
                                    error = result.message
                                )
                            }
                        }

                        else -> Unit
                    }
                }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateProfilePic(publicId: String, file: File) {
        viewModelScope.launch {
            deleteProfilePic(publicId)
                .transform { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _loadErrorUiState.update { it.copy(loading = true, error = null) }
                        }

                        is Resource.Error -> {
                            throw Exception("Fail please try later")
                        }

                        is Resource.Success -> {
                            emit(resource.data)
                        }
                    }
                }
                .map {
                    preparePresetAndFilePart(file)
                }
                .flatMapConcat {
                    val (presetRequest, filePart) = it
                    profileService.uploadProfileToCloudinary(filePart, presetRequest)
                }
                .transform { response ->

                    when (response) {
                        is Resource.Error -> {
                            file.delete()
                            throw Exception(response.message ?: "Something went wrong")
                        }

                        is Resource.Success -> {
                            emit(response.data)
                        }

                        else -> Unit
                    }
                }
                .flatMapConcat {
                    flow {
                        val result = profileService.uploadProfilePicToFireBase(it)
                        emit(result)
                    }
                }
                .catch { err ->
                    _loadErrorUiState.update { it.copy(loading = false, error = err.message) }
                }.collect { result ->
                    file.let {
                        if (file.exists()) file.delete()
                    }
                    when (result) {
                        is Resource.Success -> {
                            _loadErrorUiState.update { it.copy(loading = false, error = null) }
                            _profileCrudState.emit(result.data)
                        }

                        is Resource.Error -> {
                            _loadErrorUiState.update {
                                it.copy(
                                    loading = false,
                                    error = result.message
                                )
                            }
                        }

                        else -> Unit
                    }
                }
        }


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun deletePic(publicId: String) {
        viewModelScope.launch {
            deleteProfilePic(publicId)
                .flowOn(Dispatchers.IO)
                .transform { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _loadErrorUiState.update { it.copy(loading = true, error = null) }
                        }

                        is Resource.Error -> {
                            throw Exception(resource.message ?: "Something went wrong")
                        }

                        is Resource.Success -> {
                            emit(resource.data)
                        }
                    }
                }
                .flatMapConcat {
                    flow {
                        val result = profileService.deleteProfilePicFirebase()
                        emit(result)
                    }
                }
                .catch { err ->
                    _loadErrorUiState.update { it.copy(loading = false, error = err.message) }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _loadErrorUiState.update { it.copy(loading = false, error = null) }
                            _profileCrudState.emit(result.data)
                        }

                        is Resource.Error -> {
                            _loadErrorUiState.update {
                                it.copy(
                                    loading = false,
                                    error = result.message
                                )
                            }
                        }

                        else -> Unit
                    }
                }
        }
    }

    private suspend fun deleteProfilePic(publicId: String): Flow<Resource<String>> {
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val apiKey = Cloudinary.API_KEY
        val secretKey = Cloudinary.SECRET_KEY
        val signature = generateSignature(timeStamp, publicId, secretKey)
        return profileService.deleteProfileCloudinary(publicId, apiKey, timeStamp, signature)
    }

    private fun preparePresetAndFilePart(file: File): Pair<RequestBody, MultipartBody.Part> {
        val preset = Cloudinary.UPLOAD_PRESET
        val presetRequest = preset.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return Pair(presetRequest, filePart)
    }

    private fun generateSignature(timeStamp: String, publicId: String, secretKey: String): String {
        val dataToSign = "public_id=$publicId&timestamp=$timeStamp$secretKey"
        val digest = MessageDigest.getInstance("SHA-1")
        val hash = digest.digest(dataToSign.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

}