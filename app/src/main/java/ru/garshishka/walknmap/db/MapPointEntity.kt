package ru.garshishka.walknmap.db

import androidx.room.Entity
import ru.garshishka.walknmap.data.MapPoint
import java.time.OffsetDateTime

@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class MapPointEntity(
    val lat: Double,
    val lon: Double,
    val timeAdded: OffsetDateTime,
) {
    fun toDto() = MapPoint(lat, lon, timeAdded)

    companion object {
        fun fromDto(dto: MapPoint) =
            MapPointEntity(dto.lat, dto.lon, dto.timeAdded)
    }
}