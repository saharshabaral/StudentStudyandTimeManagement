package com.best.studentstudyandtimemanagement

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.best.studentstudyandtimemanagement.data.User
import com.best.studentstudyandtimemanagement.database.UserDao
import com.best.studentstudyandtimemanagement.database.UserDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class StudentAppTEST {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.best.studentstudyandtimemanagement", appContext.packageName)
    }
}
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {

    private lateinit var db: UserDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUser_andRetrieveByEmail_returnsCorrectUser() = runTest {
        val user = User(email = "test@example.com", password = "12345")
        dao.insertUser(user)

        val result = dao.getUserByEmail("test@example.com")

        assertNotNull(result)
        assertEquals("test@example.com", result?.email)
        assertEquals("12345", result?.password)
    }

    @Test
    fun getUser_withCorrectCredentials_returnsUser() = runTest {
        val user = User(email = "login@example.com", password = "mypassword")
        dao.insertUser(user)

        val result = dao.getUser("login@example.com", "mypassword")

        assertNotNull(result)
        assertEquals("login@example.com", result?.email)
    }

    @Test
    fun getUser_withWrongPassword_returnsNull() = runTest {
        val user = User(email = "user@example.com", password = "correctpass")
        dao.insertUser(user)

        val result = dao.getUser("user@example.com", "wrongpass")

        assertNull(result)
    }
}

//rem
