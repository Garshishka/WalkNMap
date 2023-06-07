package ru.garshishka.walknmap.data

import kotlin.math.ceil
import kotlin.math.floor

data class MapScreenCoordinates(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
){
    override fun toString(): String {
        return "$minLat .. $maxLat || $minLon .. $maxLon"
    }
}

fun MapScreenCoordinates.roundCoordinates(): MapScreenCoordinates {
    val newMinLat = floor(this.minLat * 2000) / 2000
    val newMaxLat = ceil(this.maxLat * 2000) / 2000
    val newMinLon = floor(this.minLon * 1000) / 1000
    val newMaxLon = ceil(this.maxLon * 1000) / 1000
    return MapScreenCoordinates(newMinLat, newMaxLat, newMinLon, newMaxLon)
}
