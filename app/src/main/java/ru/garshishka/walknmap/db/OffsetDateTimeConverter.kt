package ru.garshishka.walknmap.db

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun tofOffsetDateTime(dateTime: String) : OffsetDateTime{
        return formatter.parse(dateTime,OffsetDateTime::from)
    }

    @TypeConverter
    fun fromOffsetDateTime(dateTime: OffsetDateTime): String{
        return dateTime.format(formatter)
    }

}