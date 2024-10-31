//package com.jcxdc.musium.di
//
//import com.jcxdc.musium.db.UserDatabase
//import com.jcxdc.musium.repository.DatabaseRepository
//import com.jcxdc.musium.viewmodel.DatabaseViewModel
//import org.koin.android.ext.koin.androidContext
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.dsl.module
//
//val databaseModule = module {
//    single { UserDatabase.getInstance(get()).userDao() }
//    single { DatabaseRepository(get()) }
//    viewModel { DatabaseViewModel(get()) }
//}