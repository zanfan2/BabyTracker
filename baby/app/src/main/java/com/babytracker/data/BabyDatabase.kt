package com.babytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BabyRecord::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BabyDatabase : RoomDatabase() {
    abstract fun babyRecordDao(): BabyRecordDao

    companion object {
        @Volatile
        private var INSTANCE: BabyDatabase? = null

        fun getDatabase(context: Context): BabyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BabyDatabase::class.java,
                    "baby_database"
                )
                .fallbackToDestructiveMigration()  // 数据库升级时清空旧数据
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
