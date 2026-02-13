package com.babytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "baby_records")
data class BabyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: RecordType,
    val timestamp: Long = System.currentTimeMillis(),

    // 喂奶相关字段
    val milkAmount: Int? = null,  // 毫升（配方奶）
    val milkType: MilkType? = null,  // 母乳/配方奶
    val feedingDuration: Int? = null,  // 总喂养时长（分钟）
    val breastSide: BreastSide? = null,  // 左右边：左边/右边/双边
    val leftBreastDuration: Int? = null,  // 左边时长（分钟）
    val rightBreastDuration: Int? = null,  // 右边时长（分钟）

    // 排便相关字段
    val poopColor: String? = null,  // 颜色
    val poopConsistency: String? = null,  // 稠度

    // 排尿相关字段
    val peeAmount: String? = null,  // 量(少/中/多)

    val notes: String? = null
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

enum class RecordType {
    FEEDING,  // 喂奶
    POOP,     // 拉屎
    PEE       // 拉尿
}

enum class MilkType {
    BREAST,   // 母乳
    FORMULA   // 配方奶
}

enum class BreastSide {
    LEFT,    // 左边
    RIGHT,   // 右边
    BOTH     // 双边
}
