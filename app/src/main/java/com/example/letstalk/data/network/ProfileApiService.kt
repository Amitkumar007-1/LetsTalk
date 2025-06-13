package com.example.letstalk.data.network

import com.example.letstalk.data.model.ImageData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileApiService {
    @Multipart
    @POST("{cloud_name}/image/upload")
    suspend fun uploadProfilePic(
        @Path("cloud_name") cloudName:String,
        @Part file:MultipartBody.Part,
        @Part ("upload_preset") preset:RequestBody
    ):Response<ImageData>

    @FormUrlEncoded
    @POST("{cloud_name}/image/destroy")
    suspend fun deletePic(
        @Path("cloud_name") cloudName:String,
        @Field("public_id") publicId:String,
        @Field("api_key") apiKey:String,
        @Field("timestamp") timestamp:String,
        @Field("signature") signature:String
    ):Response<ResponseBody>

    @Multipart
    @POST("{cloud_name}/image/upload")
    suspend fun updateProfilePic(
        @Path("cloud_name") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part ("upload_preset") preset: RequestBody,
        @Part("public_id") publicId: RequestBody,
        @Part("overwrite") overwrite: RequestBody
    ):Response<ImageData>

}