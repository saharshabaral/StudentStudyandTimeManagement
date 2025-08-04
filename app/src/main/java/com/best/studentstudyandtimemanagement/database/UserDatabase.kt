package com.best.studentstudyandtimemanagement.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.best.studentstudyandtimemanagement.data.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

/*
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE users ADD COLUMN level TEXT NOT NULL DEFAULT '1'")
    }
}

 */
/*
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE users ADD COLUMN score INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE users ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE users ADD COLUMN date NOT NULL  TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    }
}

 */

