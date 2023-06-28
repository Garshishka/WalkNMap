package ru.garshishka.walknmap

import android.graphics.Color
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.makePointMatrix
import ru.garshishka.walknmap.data.makePolygonPointsLists
import ru.garshishka.walknmap.data.makeWallsMatrix

//TODO MOVE THEM BACK TO GLOBAL VARIABLES
var SQUARE_COLOR = Color.argb(60, 43, 255, 251)
var FOG_COLOR = Color.argb(200, 0, 0, 0)

class Test {
    fun test() {
        val pointList = listOf(
            MapPoint(55.756, 37.754),
            MapPoint(55.757, 37.754),
            MapPoint(55.758, 37.754),
            MapPoint(55.759, 37.754),
            MapPoint(55.756, 37.756),
            MapPoint(55.759, 37.756),
            MapPoint(55.756, 37.758),
            MapPoint(55.759, 37.758),
            MapPoint(55.756, 37.76),
            MapPoint(55.757, 37.76),
            MapPoint(55.758, 37.76),
            MapPoint(55.759, 37.76),
            MapPoint(55.758,37.764),
            MapPoint(55.757,37.764),
            MapPoint(55.757,37.766),
            MapPoint(55.756,37.766),
            MapPoint(55.757,37.768),
            MapPoint(55.756,37.768),
        )

        val minPoint = MapPoint(pointList.minBy { it.lat }.lat, pointList.minBy { it.lon }.lon)
        val maxPoint = MapPoint(pointList.maxBy { it.lat }.lat, pointList.maxBy { it.lon }.lon)

        val rows = ((maxPoint.lat - minPoint.lat) / DOUBLE_LAT_ADJUSTMENT).toInt() + 1
        val cols = ((maxPoint.lon - minPoint.lon) / DOUBLE_LON_ADJUSTMENT).toInt() + 1
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
    }
}