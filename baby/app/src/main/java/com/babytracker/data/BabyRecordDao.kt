package com.babytracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BabyRecordDao {
    @Insert
    suspend fun insert(record: BabyRecord): Long

    @Update
    suspend fun update(record: BabyRecord)

    @Delete
    suspend fun delete(record: BabyRecord)

    @Query("SELECT * FROM baby_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<BabyRecord>>

    @Query("SELECT * FROM baby_records WHERE type = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: RecordType): LiveData<List<BabyRecord>>

    @Query("SELECT * FROM baby_records WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getRecordsByDateRange(startTime: Long, endTime: Long): LiveData<List<BabyRecord>>

    @Query("SELECT * FROM baby_records WHERE type = :type AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getRecordsByTypeAndDateRange(type: RecordType, startTime: Long, endTime: Long): List<BabyRecord>

    @Query("SELECT COUNT(*) FROM baby_records WHERE type = :type AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getCountByTypeAndDateRange(type: RecordType, startTime: Long, endTime: Long): Int

    @Query("SELECT SUM(milkAmount) FROM baby_records WHERE type = 'FEEDING' AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getTotalMilkAmount(startTime: Long, endTime: Long): Int?

    @Query("DELETE FROM baby_records")
    suspend fun deleteAll()
}
