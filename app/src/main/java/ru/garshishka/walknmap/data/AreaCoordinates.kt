package ru.garshishka.walknmap.data

import android.graphics.Color
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolygonMapObject
import ru.garshishka.walknmap.*
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

fun AreaCoordinates.makeInnerSquareForPolygon(): List<LinearRing> {
    return listOf(
        LinearRing(
            listOf(
                Point(
                    this.maxLat + LAT_ADJUSTMENT,
                    this.minLon - LON_ADJUSTMENT
                ),
                Point(
                    this.minLat - LAT_ADJUSTMENT,
                    this.minLon - LON_ADJUSTMENT
                ),
                Point(
                    this.minLat - LAT_ADJUSTMENT,
                    this.maxLon + LON_ADJUSTMENT
                ),
                Point(
                    this.maxLat + LAT_ADJUSTMENT,
                    this.maxLon + LON_ADJUSTMENT
                ),
            )
        )
    )
}

fun AreaCoordinates.makeBoundingPolygon(
    mapObjectCollection: MapObjectCollection
): PolygonMapObject {
    return mapObjectCollection.addPolygon(
        Polygon(
            LinearRing(
                listOf(
                    Point(TOP_LAT, LEFT_LON),
                    Point(BOTTOM_LAT, LEFT_LON),
                    Point(BOTTOM_LAT, RIGHT_LON),
                    Point(TOP_LAT, RIGHT_LON)
                )
            ),
            this.makeInnerSquareForPolygon()
        )
    ).also {
        it.fillColor = FOG_COLOR
        it.strokeColor = Color.TRANSPARENT
    }
}