package com.babytracker.data

import androidx.lifecycle.LiveData
import java.util.*

class BabyRepository(private val dao: BabyRecordDao) {

    val allRecords: LiveData<List<BabyRecord>> = dao.getAllRecords()

    suspend fun insert(record: BabyRecord) {
        dao.insert(record)
    }

    suspend fun update(record: BabyRecord) {
        dao.update(record)
    }

    suspend fun delete(record: BabyRecord) {
        dao.delete(record)
    }

    fun getRecordsByType(type: RecordType): LiveData<List<BabyRecord>> {
        return dao.getRecordsByType(type)
    }

    fun getRecordsByDateRange(startTime: Long, endTime: Long): LiveData<List<BabyRecord>> {
        return dao.getRecordsByDateRange(startTime, endTime)
    }

    suspend fun getDailyStats(type: RecordType, date: Date = Date()): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endTime = calendar.timeInMillis

        return dao.getCountByTypeAndDateRange(type, startTime, endTime)
    }

    suspend fun getDailyMilkAmount(date: Date = Date()): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endTime = calendar.timeInMillis

        return dao.getTotalMilkAmount(startTime, endTime) ?: 0
    }

    suspend fun getWeeklyStats(type: RecordType): Map<String, Int> {
        val calendar = Calendar.getInstance()
        val results = mutableMapOf<String, Int>()

        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endTime = calendar.timeInMillis

            val count = dao.getCountByTypeAndDateRange(type, startTime, endTime)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
            results[dayOfWeek] = count
        }

        return results
    }
}
