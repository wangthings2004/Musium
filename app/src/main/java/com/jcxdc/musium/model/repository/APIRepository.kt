package com.jcxdc.musium.model.repository

import com.jcxdc.musium.model.api.APIServices
import com.jcxdc.musium.db.RemoteAudio
import com.jcxdc.musium.db.RemoteAudioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject


class APIRepository @Inject constructor(
    private var apiServices: APIServices
) {
    suspend fun getRemoteAudios(): List<RemoteAudioItem>? {
        val response = apiServices.getRemoteAudios()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}

