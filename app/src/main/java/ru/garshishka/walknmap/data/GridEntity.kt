package ru.garshishka.walknmap.data

import androidx.room.Entity

@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class GridEntity(
    val lat: Double,
    val lon: Double,
) {
    fun toDto() = MapPoint(lat, lon)

    companion object {
        fun fromDto(dto: MapPoint) =
            GridEntity(dto.lat, dto.lon)
    }
}