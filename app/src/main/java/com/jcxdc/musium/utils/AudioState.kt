package com.jcxdc.musium.utils

sealed class RemoteAudioState<out T> {
    data class Success<T> (val data:T): RemoteAudioState<T>()
    data class Error (val message:String): RemoteAudioState<Nothing>()
    object Loading : RemoteAudioState<Nothing>()
}