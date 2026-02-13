package com.babytracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.babytracker.data.*
import kotlinx.coroutines.launch
import java.util.*

class BabyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BabyRepository
    val allRecords: LiveData<List<BabyRecord>>

    init {
        val dao = BabyDatabase.getDatabase(application).babyRecordDao()
        repository = BabyRepository(dao)
        allRecords = repository.allRecords
    }

    fun insert(record: BabyRecord) = viewModelScope.launch {
        repository.insert(record)
    }

    fun update(record: BabyRecord) = viewModelScope.launch {
        repository.update(record)
    }

    fun delete(record: BabyRecord) = viewModelScope.launch {
        repository.delete(record)
    }

    fun getRecordsByType(type: RecordType): LiveData<List<BabyRecord>> {
        return repository.getRecordsByType(type)
    }

    fun getRecordsByDateRange(startTime: Long, endTime: Long): LiveData<List<BabyRecord>> {
        return repository.getRecordsByDateRange(startTime, endTime)
    }

    suspend fun getDailyStats(type: RecordType, date: Date = Date()): Int {
        return repository.getDailyStats(type, date)
    }

    suspend fun getDailyMilkAmount(date: Date = Date()): Int {
        return repository.getDailyMilkAmount(date)
    }

    suspend fun getWeeklyStats(type: RecordType): Map<String, Int> {
        return repository.getWeeklyStats(type)
    }
}
