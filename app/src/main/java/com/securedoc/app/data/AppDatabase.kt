package com.securedoc.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "securedoc.db"
                    ).build()
                }
            }
        }

        fun getInstance(): AppDatabase {
            return instance ?: error("Database not initialized")
        }
    }
}
