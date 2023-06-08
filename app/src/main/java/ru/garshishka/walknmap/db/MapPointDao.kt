package ru.garshishka.walknmap.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MapPointDao {
    @Query("SELECT * FROM location_table")
    fun getAll(): LiveData<List<MapPointEntity>>

    @Query("SELECT * FROM location_table WHERE lat >= :latMin AND lat <= :latMax AND lon >= :lonMin AND lon <= :lonMax")
    fun findPointsInArea(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double
    ): List<MapPointEntity>

    @Query("SELECT * FROM location_table WHERE lat = :lat AND lon = :lon")
    fun findPoint(lat: Double, lon: Double): MapPointEntity?

    @Upsert
    suspend fun save(point: MapPointEntity)

    @Query("DELETE FROM location_table WHERE lat = :lat AND lon = :lon")
    fun deleteById(lat: Double, lon: Double)
}