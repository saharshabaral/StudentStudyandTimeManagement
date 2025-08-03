package com.best.studentstudyandtimemanagement.repository

import com.best.studentstudyandtimemanagement.database.UserDao
import com.best.studentstudyandtimemanagement.data.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }
}
