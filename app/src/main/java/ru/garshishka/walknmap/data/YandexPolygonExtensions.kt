package ru.garshishka.walknmap.data

import android.graphics.Color
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolygonMapObject
import ru.garshishka.walknmap.*

fun PolygonMapObject.checkCentralPoint(centralPoint: Point): Boolean =
    this.geometry.outerRing.points[0].latitude - LAT_ADJUSTMENT == centralPoint.latitude
            && this.geometry.outerRing.points[0].longitude - LON_ADJUSTMENT == centralPoint.longitude

fun Polygon.equalsOtherPolygon(polygon: Polygon): Boolean {
    val thisPoints = this.outerRing.points.map { it.toMapPoint() }
    val otherPoints = polygon.outerRing.points.map { it.toMapPoint() }
    return thisPoints == otherPoints
}

fun List<Polygon>.filterOtherPolygonList(other: List<Polygon>): List<Polygon> {
    val result = mutableListOf<Polygon>()
    this.forEach newPolygon@{ thisPolygon ->
        var noEqual = true
        other.forEach { otherPolygon ->
            if (thisPolygon.equalsOtherPolygon(otherPolygon)) {
                noEqual = false
                return@newPolygon
            }
        }
        if (noEqual) {
            result.add(thisPolygon)
        }
    }
    return result
}

fun makeBoundingPolygon(
    mapObjectCollection: MapObjectCollection,
    innerRings: List<LinearRing>,
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
            innerRings
        )
    ).also {
        it.fillColor = FOG_COLOR
        it.strokeColor = Color.TRANSPARENT
    }
}

fun makeInsidePolygon(
    polygon: LinearRing,
): Polygon = Polygon(
    polygon,
    ArrayList<LinearRing>() //TODO put inside polygons holes here
)