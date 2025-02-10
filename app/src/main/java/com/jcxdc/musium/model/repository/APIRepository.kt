package com.jcxdc.musium.model.repository

import com.jcxdc.musium.model.api.APIServices
import com.jcxdc.musium.db.AudioItem
import javax.inject.Inject


class APIRepository @Inject constructor(
    private var apiServices: APIServices
) {
    suspend fun getRemoteAudios(): List<AudioItem>? {
        val response = apiServices.getRemoteAudios()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}

