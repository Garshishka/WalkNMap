package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point

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
