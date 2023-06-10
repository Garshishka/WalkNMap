package ru.garshishka.walknmap.data

import android.graphics.Color
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.MapObjectCollection
import ru.garshishka.walknmap.*
import kotlin.math.round

fun Point.roundCoordinates(): Point {
    return Point(
        round(this.latitude * LAT_ROUNDER) / LAT_ROUNDER,
        round(this.longitude * LON_ROUNDER) / LON_ROUNDER
    )
}

fun Point.addSquare(mapObjectCollection: MapObjectCollection, isFog : Boolean = false) {
    val rect = mapObjectCollection.addPolygon(
        this.makeSquarePolygon()
    )
    rect.strokeColor = Color.TRANSPARENT
    rect.fillColor = if (isFog) FOG_COLOR else SQUARE_COLOR
}

fun Point.makeSquarePolygon(): Polygon {
    return Polygon(
        LinearRing(
            listOf(
                Point(this.latitude - LAT_ADJUSTMENT, this.longitude - LON_ADJUSTMENT),
                Point(this.latitude - LAT_ADJUSTMENT, this.longitude + LON_ADJUSTMENT),
                Point(this.latitude + LAT_ADJUSTMENT, this.longitude + LON_ADJUSTMENT),
                Point(this.latitude + LAT_ADJUSTMENT, this.longitude - LON_ADJUSTMENT)
            )
        ),
        ArrayList<LinearRing>()
    )
}