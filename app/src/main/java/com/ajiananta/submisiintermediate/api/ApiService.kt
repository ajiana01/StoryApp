package com.ajiananta.submisiintermediate.api


import com.ajiananta.submisiintermediate.api.response.FileUploadResponse
import com.ajiananta.submisiintermediate.api.response.LoginResponse
import com.ajiananta.submisiintermediate.api.response.RegisterResponse
import com.ajiananta.submisiintermediate.api.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getListStories(
        @Header("Authorization") bearer: String?,
        @QueryMap queries: Map<String, Int>,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lon") lon: RequestBody?,
        @Part("lat") lat: RequestBody?
    ): Call<FileUploadResponse>

    @GET("stories?location=1")
    fun getLocStories(
        @Header("Authorization") bearer: String?
    ): Call<StoriesResponse>
}