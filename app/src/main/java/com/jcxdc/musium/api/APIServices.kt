package com.jcxdc.musium.api


import com.jcxdc.musium.db.RemoteAudio
import retrofit2.Call
import retrofit2.http.GET

interface APIServices {
    @GET("techtrek/Remote_audio.json")
    suspend fun getRemoteAudio(): RemoteAudio
}