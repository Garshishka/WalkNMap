package ru.garshishka.walknmap.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MapPointEntity::class], version = 3, exportSchema = false)
@TypeConverters(OffsetDateTimeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun mapPointDao(): MapPointDao
}