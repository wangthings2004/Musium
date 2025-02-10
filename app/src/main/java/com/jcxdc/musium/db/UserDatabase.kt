package com.jcxdc.musium.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jcxdc.musium.utils.Constants.USER_DATABASE

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDatabase :RoomDatabase(){
    abstract fun userDao():UserDao
    companion object{
        @Volatile
        private var instance: UserDatabase?= null
        fun getInstance(context: Context) : UserDatabase{
            if (instance==null){
                instance= Room.databaseBuilder(context,UserDatabase::class.java,USER_DATABASE).build()
            }
            return instance!!
        }
    }
}