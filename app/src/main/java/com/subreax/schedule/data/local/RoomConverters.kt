package com.subreax.schedule.data.local

import androidx.room.TypeConverter
import com.subreax.schedule.data.model.ScheduleType
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun fromOrdinalScheduleType(typeOrd: Int): ScheduleType {
        return ScheduleType.entries[typeOrd]
    }

    @TypeConverter
    fun scheduleTypeToOrdinal(scheduleType: ScheduleType): Int {
        return scheduleType.ordinal
    }

    @TypeConverter
    fun fromTimestamp(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}