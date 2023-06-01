package ru.garshishka.walknmap.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GridPointDao {
    @Query("SELECT * FROM location_table")
    fun getAll(): LiveData<List<GridEntity>>

    @Query("SELECT * FROM location_table WHERE lat = :lat AND lon = :lon")
    fun findPoint(lat: Double, lon: Double) : GridEntity

    @Upsert
    suspend fun save(point: GridEntity)

    @Query("DELETE FROM location_table WHERE lat = :lat AND lon = :lon")
    fun deleteById(lat: Double, lon: Double)
}