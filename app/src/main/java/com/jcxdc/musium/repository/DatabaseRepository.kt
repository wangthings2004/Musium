package com.jcxdc.musium.repository

import com.jcxdc.musium.db.UserDao
import com.jcxdc.musium.db.UserEntity


class DatabaseRepository(private val dao: UserDao) {
    suspend fun insertUser(userEntity: UserEntity) = dao.insertUser(userEntity)
    suspend fun getUserByUsername(username: String) = dao.getUserByUsername(username)
}
