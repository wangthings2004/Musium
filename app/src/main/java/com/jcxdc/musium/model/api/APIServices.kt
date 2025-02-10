package com.jcxdc.musium.model.api


import com.jcxdc.musium.db.AudioItem
import retrofit2.Response
import retrofit2.http.GET

interface APIServices {
    @GET("techtrek/Remote_audio.json")
    suspend fun getRemoteAudios(): Response<List<AudioItem>>
}