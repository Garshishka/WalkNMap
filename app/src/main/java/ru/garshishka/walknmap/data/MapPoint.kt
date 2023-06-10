package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import ru.garshishka.walknmap.LAT_ROUNDER
import ru.garshishka.walknmap.LON_ROUNDER
import java.time.OffsetDateTime
import kotlin.math.round

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
        if (other is MapPoint){
            return (this.lat == other.lat && this.lon == other.lon)
        } else {
            return super.equals(other)
        }
    }
}

fun MapPoint.toYandexPoint(): Point{
    return Point(this.lat, this.lon)
}

fun MapPoint.roundCoordinates(): MapPoint {
    return MapPoint(
        round(this.lat* LAT_ROUNDER) / LAT_ROUNDER,
        round(this.lon * LON_ROUNDER) / LON_ROUNDER
    )
}
