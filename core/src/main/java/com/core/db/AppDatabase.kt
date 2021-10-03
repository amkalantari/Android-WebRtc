package com.core.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.core.dto.tables.TestDb


@Database(
    entities = [
        TestDb::class
    ], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun daoTest() : DaoTest

    companion object {

        @VisibleForTesting
        const val DATABASE_NAME = "appDataBase"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, testMode: Boolean): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, testMode).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(context: Context, testMode: Boolean): AppDatabase {
            return if (testMode) {
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            } else {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}