package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import java.time.OffsetDateTime

//Main data class for storing information about visited squares
data class MapPoint(
    val lat: Double,
    val lon: Double,
    val timeAdded: OffsetDateTime,
){
    override fun toString(): String {
        return "$lat | $lon"
    }
}

fun MapPoint.toYandexPoint(): Point{
    return Point(this.lat, this.lon)
}
