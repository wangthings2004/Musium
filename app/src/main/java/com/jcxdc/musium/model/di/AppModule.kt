package com.jcxdc.musium.model.di

import android.content.ContentResolver
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jcxdc.musium.content_provider.SongDataSource
import com.jcxdc.musium.db.PlaylistDao
import com.jcxdc.musium.db.PlaylistDatabase
import com.jcxdc.musium.db.SongDao
import com.jcxdc.musium.db.SongDatabase
import com.jcxdc.musium.model.api.APIServices
import com.jcxdc.musium.model.repository.PlaylistRepository
import com.jcxdc.musium.model.repository.SongRepository
import com.jcxdc.musium.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val appModule = module {
    single { (context: Context) -> context.contentResolver }
    single { SongDataSource(get()) }
    single { BASE_URL }
    single { GsonBuilder().setLenient().create() }

    // Thêm HttpLoggingInterceptor vào Koin
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // Khởi tạo OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single { get<Retrofit>().create(APIServices::class.java) }
    single { APIRepository(get()) }
    single { PlaylistDatabase.getInstance(get()) }
    single { get<PlaylistDatabase>().playlistDao() }
    single { PlaylistRepository(get()) }
    single { SongDatabase.getInstance(get()) }
    single { get<SongDatabase>().songDao() }
    single { SongRepository(get()) }

    viewModel { RemoteAudioViewModel(get()) }
    single { RemoteAudioAdapter() }
    single { PlaylistAdapter() }
    single { TopAlbumAdapter() }

    viewModel { PlaylistViewModel(get()) }
    viewModel { SongViewModel(get()) }
    viewModel { LocalAudioViewModel(get()) }

    single { LocalMusicAdapter() }
    single { AddPlaylistAdapter() }
}

