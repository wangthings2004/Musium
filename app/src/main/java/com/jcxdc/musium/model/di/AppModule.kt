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
import com.jcxdc.musium.model.repository.APIRepository
import com.jcxdc.musium.model.repository.PlaylistRepository
import com.jcxdc.musium.model.repository.SongRepository
import com.jcxdc.musium.ui.screen.home.RemoteAudioAdapter
import com.jcxdc.musium.ui.screen.home.TopAlbumAdapter
import com.jcxdc.musium.ui.screen.library.AddPlaylistAdapter
import com.jcxdc.musium.ui.screen.library.LocalMusicAdapter
import com.jcxdc.musium.ui.screen.playlist.PlaylistAdapter
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel
import com.jcxdc.musium.ui.viewmodel.PlaylistViewModel
import com.jcxdc.musium.ui.viewmodel.RemoteAudioViewModel
import com.jcxdc.musium.ui.viewmodel.SongViewModel
import com.jcxdc.musium.utils.Constants.BASE_URL

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {

    single { androidContext().contentResolver }

    single { SongDataSource(get()) }
    single { BASE_URL }
    single { GsonBuilder().setLenient().create() }


    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }


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

