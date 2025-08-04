package com.best.studentstudyandtimemanagement.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val level: String ="1",
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val date: Long = System.currentTimeMillis()
)
