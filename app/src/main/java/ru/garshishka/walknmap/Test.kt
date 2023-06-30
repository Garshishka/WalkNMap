package ru.garshishka.walknmap

import android.graphics.Color
import ru.garshishka.walknmap.data.*
import kotlin.math.roundToInt

//TODO MOVE THEM BACK TO GLOBAL VARIABLES
var SQUARE_COLOR = Color.argb(60, 43, 255, 251)
var FOG_COLOR = Color.argb(200, 0, 0, 0)


fun test() {
    val pointList = listOf(
        MapPoint(55.753, 37.754),
        MapPoint(55.754, 37.752),
        MapPoint(55.755, 37.752),
        MapPoint(55.756, 37.752),
        MapPoint(55.756, 37.754),
        MapPoint(55.757, 37.752),
        MapPoint(55.757, 37.754),
    )

    val minPoint = MapPoint(pointList.minBy { it.lat }.lat, pointList.minBy { it.lon }.lon)
    val maxPoint = MapPoint(pointList.maxBy { it.lat }.lat, pointList.maxBy { it.lon }.lon)

    val rows =
        ((maxPoint.lat - minPoint.lat) / DOUBLE_LAT_ADJUSTMENT).roundToInt() + 1
    val cols =
        ((maxPoint.lon - minPoint.lon) / DOUBLE_LON_ADJUSTMENT).roundToInt() + 1
    println("$rows | $cols")

    val pointMatrix = pointList.makePointMatrix(minPoint, rows, cols)
    for (row in pointMatrix) {
        println(row.contentToString())
    }

    val wallMatrix = pointMatrix.makeWallsMatrix(rows, cols)
    for (row in wallMatrix) {
        println(row.contentToString())
    }

    val result = wallMatrix.makePolygonPointsLists(rows, cols)

    result.forEach { println(it) }

    val insidePolygons = mutableListOf<List<Pair<Int, Int>>>()

    val iterator = result.listIterator()
    while (iterator.hasNext()) {
        val polygon = iterator.next()
        run breaking@{
            result.filterNot { it == polygon }.forEach { other ->
                if (polygon.isInsideOtherPolygon(other)) {
                    iterator.remove()
                    insidePolygons.add(polygon)
                    return@breaking
                }
            }
        }
    }

    result.forEach { println(it) }
    println("inside")
    insidePolygons.forEach { println(it) }

    val ar = ArrayList(result.map { list ->
        list.map {
            MapPoint(
                ((minPoint.lat + ((it.first - 1) * 2 * LAT_ADJUSTMENT)) - LAT_ADJUSTMENT).roundForCoordinates(
                    true, true
                ),
                (minPoint.lon + ((it.second - 1) * 2 * LON_ADJUSTMENT) - LON_ADJUSTMENT).roundForCoordinates(
                    false, true
                )
            )
        }
    })

    ar.forEach { println(it) }

}
