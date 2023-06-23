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
    val top = round((this.lat + LAT_ADJUSTMENT) * DOUBLE_LAT_ROUNDER) / DOUBLE_LAT_ROUNDER
    val bottom = round((this.lat - LAT_ADJUSTMENT) * DOUBLE_LAT_ROUNDER) / DOUBLE_LAT_ROUNDER
    val right = round((this.lon + LON_ADJUSTMENT) * DOUBLE_LON_ROUNDER) / DOUBLE_LON_ROUNDER
    val left = round((this.lon - LON_ADJUSTMENT) * DOUBLE_LON_ROUNDER) / DOUBLE_LON_ROUNDER
    return MapPolygon(
        mutableSetOf(
            MapPoint(bottom, left),
            MapPoint(bottom, right),
            MapPoint(top, right),
            MapPoint(top, left)
        )
    )
}

fun MapPoint.distanceToOtherPoint(other: MapPoint): Double {
    val distLat = ((this.lat - other.lat) * DOUBLE_LAT_ROUNDER) / DOUBLE_LAT_ROUNDER
    val distLon = ((this.lon - other.lon) * DOUBLE_LON_ROUNDER) / DOUBLE_LON_ROUNDER
    return (distLat * distLat) + (distLon * distLon)
}

fun MapPoint.checkIfPointNotBehindAlreadyPlacedPoint(
    sortedSet: Set<MapPoint>,
    anchorPoint: MapPoint,
    lonMovement: Boolean,
): Boolean {
    //we take sorted points and find ones that are on this movement axis
    sortedSet.filter {
        if (lonMovement) {
            it.lat == this.lat
        } else {
            it.lon == this.lon
        }
    }
        .forEach { sortedPoint ->
            if (this.checkIfBehindPoint(anchorPoint, sortedPoint, lonMovement)) {
                return false
            }
        }
    return true
}

fun MapPoint.checkIfBehindPoint(
    anchorPoint: MapPoint,
    otherPoint: MapPoint,
    lonMovement: Boolean
): Boolean {
    if(lonMovement){
        if (oneOrAnotherPointIsBetween(
                anchorPoint.lon,
                this.lon,
                otherPoint.lon
            )
        ) {
            return true
        }
    } else{
        if (oneOrAnotherPointIsBetween(
                anchorPoint.lat,
                this.lat,
                otherPoint.lat
            )
        ) {
            return true
        }
    }
    return false
}

