package ru.garshishka.walknmap.data

import ru.garshishka.walknmap.LAT_ADJUSTMENT
import ru.garshishka.walknmap.LAT_ROUNDER
import ru.garshishka.walknmap.LON_ADJUSTMENT
import ru.garshishka.walknmap.LON_ROUNDER
import kotlin.math.ceil
import kotlin.math.floor

data class AreaCoordinates(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
) {
    override fun toString(): String {
        return "$minLat .. $maxLat || $minLon .. $maxLon"
    }
}

fun AreaCoordinates.roundCoordinates(): AreaCoordinates = AreaCoordinates(
    floor(this.minLat * LAT_ROUNDER) / LAT_ROUNDER,
    ceil(this.maxLat * LAT_ROUNDER) / LAT_ROUNDER,
    floor(this.minLon * LON_ROUNDER) / LON_ROUNDER,
    ceil(this.maxLon * LON_ROUNDER) / LON_ROUNDER
)

fun AreaCoordinates.toZero() = AreaCoordinates(0.0, 0.0, 0.0, 0.0)

fun AreaCoordinates.makePointList(): List<MapPoint> {
    val list = mutableListOf<MapPoint>()
    if (this != AreaCoordinates(0.0, 0.0, 0.0, 0.0)) {
        var i = minLat
        while (i < maxLat + LAT_ADJUSTMENT) {
            var j = minLon
            while (j < maxLon + LON_ADJUSTMENT) {
                list.add(MapPoint(i, j).roundCoordinates())
                j += LON_ADJUSTMENT + LON_ADJUSTMENT
            }
            i += LAT_ADJUSTMENT + LAT_ADJUSTMENT
        }
    }
    return list
}

fun AreaCoordinates.isPointOutside(point: MapPoint): Boolean =
    this.minLat > point.lat || this.maxLat < point.lat || this.minLon > point.lon || this.maxLon < point.lon
