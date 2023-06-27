package ru.garshishka.walknmap

import android.graphics.Color
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.makePointMatrix
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

//val result = wallMatrix.makePolygonPointsLists(rows, cols)

        val result = mutableListOf<MutableList<Pair<Int, Int>>>()

        val indexMatrix = Array(rows + 2) { r ->
            IntArray(cols + 2) { c -> -1 }
        }
        for (row in indexMatrix) {
            println(row.contentToString())
        }

        for (r in 0..rows + 1) {
            for (c in 0..cols + 1) {
                if ((wallMatrix[r][c] and 1) == 1) {
                    var i = r + 1
                    var j = c
                    val cycle = mutableListOf((i to j))
                    indexMatrix[i][j] = 0
                    while (true) {
                        if (i < rows + 1 && (wallMatrix[i][j - 1] and 2) == 2) {
                            wallMatrix[i][j - 1] -= 2
                            i++
                        } else if (i > 0 && (wallMatrix[i - 1][j - 1] and 2) == 2) {
                            wallMatrix[i - 1][j - 1] -= 2
                            i--
                        } else if (j < cols + 1 && (wallMatrix[i - 1][j] and 1 == 1)) {
                            wallMatrix[i - 1][j] -= 1
                            j++
                        } else if (j > 0 && (wallMatrix[i - 1][j - 1] and 1 == 1)) {
                            wallMatrix[i - 1][j - 1] -= 1
                            j--
                        } else {
                            break
                        }
                        cycle.add(i to j)
                        val ix = indexMatrix[i][j]
                        if (ix >= 0) {

                        }
                    }
                    // result.add(cycle)
                }
            }
        }

        for (row in wallMatrix) {
            println(row.contentToString())
        }

        result.forEach { println(it) }
    }
}