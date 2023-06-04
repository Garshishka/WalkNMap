package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import kotlin.math.round

data class MapPoint(
    val lat: Double,
    val lon: Double,
){
    override fun toString(): String {
        return "$lat | $lon"
    }
}

fun MapPoint.toYandexPoint(): Point{
    return Point(this.lat, this.lon)
}

fun Point.roundCoordinates(): Point{
    val newLat = round(this.latitude * 2000) / 2000
    val newLon = round(this.longitude * 1000) / 1000
    return Point(newLat, newLon)
}