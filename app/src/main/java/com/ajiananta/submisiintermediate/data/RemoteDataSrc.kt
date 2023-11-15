package com.ajiananta.submisiintermediate.data

import androidx.lifecycle.MutableLiveData
import com.ajiananta.submisiintermediate.api.ApiConfig
import com.ajiananta.submisiintermediate.api.response.FileUploadResponse
import com.ajiananta.submisiintermediate.api.response.LoginResponse
import com.ajiananta.submisiintermediate.api.response.RegisterResponse
import com.ajiananta.submisiintermediate.api.response.StoriesResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSrc {
    val error = MutableLiveData("")
    var responsecode = ""

    fun login(callback: LoginCallback, email: String, password: String) {
        callback.onLogin(
            LoginResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if(response.isSuccessful){
                    response.body()?.let { callback.onLogin(it) }
                }else {
                    when (response.code()) {
                        200 -> responsecode = "200"
                        400 -> responsecode = "400"
                        401 -> responsecode = "401"
                        else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                    }
                    callback.onLogin(
                        LoginResponse(
                            null,
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onLogin(
                    LoginResponse(
                        null,
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun register(callback: RegisterCallback, name: String, email: String, password: String) {
        val registInfo = RegisterResponse(
            true,
            ""
        )
        callback.onRegister(
            registInfo
        )
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object: Callback<RegisterResponse> {
override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onRegister(it) }
                    responsecode = "201"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }else {
                    responsecode = "400"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback.onRegister(
                    RegisterResponse(
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun uploadStories(callback: UploadStoryCallback, token: String, imageFile: File, desc: String, lon: String? = null, lat: String? = null) {
        callback.onUploadStory(
            uploadStoryResponse = FileUploadResponse(
                true,
                ""
            )
        )

        val descript = desc.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val reqImgFile = imageFile.asRequestBody("image/*".toMediaType())
        val imgMulti: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            reqImgFile
        )
        val client = ApiConfig.getApiService().uploadImage(bearer = "Bearer $token", imgMulti, descript, latitude, longitude)
        client.enqueue(object: Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.error) {
                        callback.onUploadStory(responseBody)
                    } else {
                        callback.onUploadStory(
                            uploadStoryResponse = FileUploadResponse(
                                true,
                                "Gagal Mengunggah File!"
                                //context.getString(R.string.fail_upload_file),
                            )
                        )
                    }
                } else {
                    callback.onUploadStory(
                        uploadStoryResponse = FileUploadResponse(
                            true,
                            "Gagal Mengunggah File!"
                            //context.getString(R.string.fail_upload_file),
                        )
                    )
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                callback.onUploadStory(
                    uploadStoryResponse = FileUploadResponse(
                        true,
                        "Gagal Mengunggah File!"
                    )
                )
            }
        })
    }

    fun getMapsStories(callback: GetMapsStoryCallback, token: String){
        val client = ApiConfig.getApiService().getLocStories(bearer = "Bearer $token")
        client.enqueue(object: Callback<StoriesResponse>{
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { callback.onGetMapsStory(it) }
                } else {
                    val storiesResponse = StoriesResponse(
                        emptyList(),
                        true,
                        "Gagal Mengambil Data!"
                    )
                    callback.onGetMapsStory(storiesResponse)
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                val storiesResponse = StoriesResponse(
                    emptyList(),
                    true,
                    "Gagal Mengambil Data!"
                )
                callback.onGetMapsStory(storiesResponse)
            }
        })
    }

    interface LoginCallback {
        fun onLogin(loginResponse: LoginResponse)
    }

    interface RegisterCallback {
        fun onRegister(registerResponse: RegisterResponse)
    }

    interface UploadStoryCallback {
        fun onUploadStory(uploadStoryResponse: FileUploadResponse)
    }

    interface GetMapsStoryCallback {
        fun onGetMapsStory(storiesResponse: StoriesResponse)
    }

    companion object {
        @Volatile
        private var INSTANCE: RemoteDataSrc? = null
        fun getInstance(): RemoteDataSrc =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteDataSrc()
            }
    }
}





