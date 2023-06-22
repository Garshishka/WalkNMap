package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import ru.garshishka.walknmap.*
import java.time.OffsetDateTime
import kotlin.math.round

//Main data class for storing information about visited squares
data class MapPoint(
    val lat: Double,
    val lon: Double,
    val timeAdded: OffsetDateTime = OffsetDateTime.now(),
) {
    override fun toString(): String {
        return "$lat | $lon"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is MapPoint) {
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

fun MapPoint.toYandexPoint(): Point {
    return Point(this.lat, this.lon)
}

fun MapPoint.roundCoordinates(): MapPoint {
    return MapPoint(
        round(this.lat * LAT_ROUNDER) / LAT_ROUNDER,
        round(this.lon * LON_ROUNDER) / LON_ROUNDER
    )
}

fun MapPoint.makeMapPolygon(): MapPolygon {
    val latNewRounder = 2 * LAT_ROUNDER
    val lonNewRounder = 2 * LON_ROUNDER
    val top = round((this.lat + LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
    val bottom = round((this.lat - LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
    val right = round((this.lon + LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
    val left = round((this.lon - LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
    return MapPolygon(
        mutableSetOf(
            MapPoint(bottom, left),
            MapPoint(bottom, right),
            MapPoint(top, right),
            MapPoint(top, left)
        )
    )
}

fun MapPoint.lessThenOtherInPolygon(
    other: MapPoint,
    centerLat: Double,
    centerLon: Double
): Boolean {
    val thisLat = round((this.lat - centerLat) * DOUBLE_LAT_ROUNDER) / DOUBLE_LAT_ROUNDER
    val thisLon = round((this.lon - centerLon) * DOUBLE_LON_ROUNDER) / DOUBLE_LON_ROUNDER
    val otherLat = round((other.lat - centerLat) * DOUBLE_LAT_ROUNDER) / DOUBLE_LAT_ROUNDER
    val otherLon = round((other.lon - centerLon) * DOUBLE_LON_ROUNDER) / DOUBLE_LON_ROUNDER

    if (thisLat >= 0 && otherLat < 0)
        return true
    if (thisLat < 0 && otherLat >= 0)
        return false
    if (thisLat == 0.0 && otherLat == 0.0) {
        if (thisLon >= 0 || otherLon >= 0) {
            return this.lon > other.lon
        }
        return other.lon > this.lon
    }

    val crossProductOfVectors = (thisLat) * (otherLon) - (otherLat) * (thisLon)
    if (crossProductOfVectors > 0) return false
    if (crossProductOfVectors < 0) return true

    val distanceThis = thisLat * thisLat + thisLon * thisLon
    val distanceOther = otherLat * otherLat + otherLon * otherLon

    return distanceThis > distanceOther
}
