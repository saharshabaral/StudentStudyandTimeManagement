package com.best.studentstudyandtimemanagement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.best.studentstudyandtimemanagement.database.UserDatabase
import com.best.studentstudyandtimemanagement.data.User
import com.best.studentstudyandtimemanagement.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = UserDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao)

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun login(email: String, password: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            callback(user)
        }
    }
}
