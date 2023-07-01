package ru.garshishka.walknmap.data

import ru.garshishka.walknmap.LAT_ROUNDER
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
