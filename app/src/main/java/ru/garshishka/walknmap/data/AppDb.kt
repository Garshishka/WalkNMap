package ru.garshishka.walknmap.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GridEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun gridPointDao(): GridPointDao
}