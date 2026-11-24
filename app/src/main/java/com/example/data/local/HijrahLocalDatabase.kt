package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedUserProfileEntity::class,
        CachedChecklistEntity::class,
        CachedDocumentEntity::class,
        CachedBudgetEntity::class,
        CachedFavoriteEntity::class,
        CachedForumPostEntity::class,
        CachedForumReplyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HijrahLocalDatabase : RoomDatabase() {
    abstract fun cacheDao(): HijrahCacheDao

    companion object {
        @Volatile
        private var instance: HijrahLocalDatabase? = null

        fun getInstance(context: Context): HijrahLocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HijrahLocalDatabase::class.java,
                    "hijrah_offline_cache.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
