package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import java.time.OffsetDateTime

//Main data class for storing information about visited squares
data class MapPoint(
    val lat: Double,
    val lon: Double,
    val timeAdded: OffsetDateTime = OffsetDateTime.now(),
){
    override fun toString(): String {
        return "$lat | $lon"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is MapPoint){
            (this.lat == other.lat && this.lon == other.lon)
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }
}

fun MapPoint.toYandexPoint(): Point{
    return Point(this.lat, this.lon)
}

