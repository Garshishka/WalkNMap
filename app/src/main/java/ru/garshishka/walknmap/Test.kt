package ru.garshishka.walknmap

import android.graphics.Color
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.makePointMatrix

//TODO MOVE THEM BACK TO GLOBAL VARIABLES
var SQUARE_COLOR = Color.argb(60, 43, 255, 251)
var FOG_COLOR = Color.argb(200, 0, 0, 0)

class Test {

    fun test() {
        val pointList = listOf(
            MapPoint(55.756, 37.754),
            MapPoint(55.757, 37.754),
            MapPoint(55.758, 37.754),
            MapPoint(55.756, 37.756),
            MapPoint(55.758, 37.756),
        )

        val pointMatrix = pointList.makePointMatrix()

        for (row in pointMatrix) {
            println(row.contentToString())
        }
    }
}