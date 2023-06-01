package ru.garshishka.walknmap.data

import androidx.room.Entity
import com.yandex.mapkit.geometry.Point

@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class GridEntity(
    //@PrimaryKey(autoGenerate = true)
    //val id: Long,
    val lat: Double,
    val lon: Double,
    val enabled: Boolean = false,
    //val name: String
) {
    fun toDto() = GridPoint(Point(lat, lon), enabled)

    companion object {
        fun fromDto(dto: GridPoint) =
            GridEntity(dto.point.latitude, dto.point.longitude, dto.enabled)
    }
}