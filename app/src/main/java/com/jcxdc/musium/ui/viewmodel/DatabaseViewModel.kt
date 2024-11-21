package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.UserEntity
import com.jcxdc.musium.model.repository.DatabaseRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseViewModel(private val repository: DatabaseRepository) : ViewModel() {
    fun registerUser(username: String, password: String, email : String,  onResult: (Boolean, String) -> Unit,) {
        viewModelScope.launch {
            val existingUser = withContext(Dispatchers.IO) {
                repository.getUserByUsername(username)
            }
            if (existingUser != null) {
                onResult(false, "Username already exists")
            } else {
                val userEntity = UserEntity(username = username, passwordHash = hashPassword(password), email = email)
                withContext(Dispatchers.IO) {
                    repository.insertUser(userEntity)
                }
                onResult(true, "Registration successful")
            }
        }
    }





        fun loginUser(userName: String, password: String, onResult: (Boolean, String) -> Unit) {
            viewModelScope.launch {
                val user = withContext(Dispatchers.IO) {
                    repository.getUserByUsername(userName)
                }
                if (user == null) {

                    onResult(false, "User not found")
                } else {
                    val hashedPassword = hashPassword(password)
                    if (user.passwordHash == hashedPassword) {
                        onResult(true, "Login successful")
                    } else {
                        onResult(false, "Invalid password")
                    }
                }
            }
        }

        private fun hashPassword(password: String): String {

            return password
        }
    }


