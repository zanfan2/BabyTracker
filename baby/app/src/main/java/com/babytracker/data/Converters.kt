package com.babytracker.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromRecordType(value: RecordType): String {
        return value.name
    }

    @TypeConverter
    fun toRecordType(value: String): RecordType {
        return RecordType.valueOf(value)
    }

    @TypeConverter
    fun fromMilkType(value: MilkType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMilkType(value: String?): MilkType? {
        return value?.let { MilkType.valueOf(it) }
    }

    @TypeConverter
    fun fromBreastSide(value: BreastSide?): String? {
        return value?.name
    }

    @TypeConverter
    fun toBreastSide(value: String?): BreastSide? {
        return value?.let { BreastSide.valueOf(it) }
    }
}
