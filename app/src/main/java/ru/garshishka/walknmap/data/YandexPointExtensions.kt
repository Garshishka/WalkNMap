package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import kotlin.math.round

fun Point.roundCoordinates(): Point {
    val newLat = round(this.latitude * 2000) / 2000
    val newLon = round(this.longitude * 1000) / 1000
    return Point(newLat, newLon)
}

fun Point.makeSquarePolygon(): Polygon {
    return Polygon(
        LinearRing(
            listOf(
                Point(this.latitude - 0.000250, this.longitude - 0.000500),
                Point(this.latitude - 0.000250, this.longitude + 0.000500),
                Point(this.latitude + 0.000250, this.longitude + 0.000500),
                Point(this.latitude + 0.000250, this.longitude - 0.000500)
            )
        ),
        ArrayList<LinearRing>()
    )
}

fun Point.checkSquareFirstCorner(squareFirstPoint: Point): Boolean =
    (this.latitude - 0.000250 == squareFirstPoint.latitude && this.longitude - 0.000500 == squareFirstPoint.longitude)
