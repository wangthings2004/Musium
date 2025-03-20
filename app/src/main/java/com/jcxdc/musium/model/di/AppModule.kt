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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }
    @Provides
    @Singleton
    fun provideSongDataSource(contentResolver: ContentResolver): SongDataSource {
        return SongDataSource(contentResolver)
    }
    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(provideBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAPIServices(retrofit: Retrofit): APIServices {
        return retrofit.create(APIServices::class.java)
    }
    @Provides
    @Singleton
    fun providePlaylistDatabase(@ApplicationContext context: Context): PlaylistDatabase {
        return PlaylistDatabase.getInstance(context)
    }

    @Provides
    fun providePlaylistDao(database: PlaylistDatabase): PlaylistDao {
        return database.playlistDao()
    }
    @Provides
    @Singleton
    fun providePlaylistRepository(dao: PlaylistDao): PlaylistRepository {
        return PlaylistRepository(dao)
    }
    @Provides
    @Singleton
    fun provideSongDatabase(@ApplicationContext context: Context): SongDatabase {
        return SongDatabase.getInstance(context)
    }

    @Provides
    fun provideSongDao(database: SongDatabase): SongDao {
        return database.songDao()
    }
    @Provides
    @Singleton
    fun provideSongRepository(dao: SongDao): SongRepository {
        return SongRepository(dao)
    }



}
