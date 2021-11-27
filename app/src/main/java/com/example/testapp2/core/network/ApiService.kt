package com.example.testapp2.core.network

import com.example.testapp2.core.model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("upload-image")
    fun upload(
            @Part image: MultipartBody.Part? = null
    ): Call<ResponseModel>
}