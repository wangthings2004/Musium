package com.jcxdc.musium.repository

import com.jcxdc.musium.api.APIServices
import com.jcxdc.musium.db.RemoteAudio
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
    fun getRemoteAudioRepo(): Flow<RemoteAudio> = flow {
        val response = apiServices.getRemoteAudio()
        emit(response)
    }.flowOn(Dispatchers.IO)
}
