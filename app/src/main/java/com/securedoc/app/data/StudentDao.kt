package com.securedoc.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM students ORDER BY username ASC")
    suspend fun getAll(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getByCredentials(username: String, password: String): StudentEntity?
}
