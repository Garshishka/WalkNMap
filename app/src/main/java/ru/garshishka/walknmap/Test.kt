package ru.garshishka.walknmap

import ru.garshishka.walknmap.data.makePolygonPointsLists
import ru.garshishka.walknmap.data.makeWallsMatrix
import ru.garshishka.walknmap.data.separateInsidePolygons

fun test() {
    val matrix = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 1, 1, 1, 0),
        intArrayOf(0, 1, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 0, 1, 0),
        intArrayOf(0, 1, 1, 0, 1, 0),
        intArrayOf(0, 0, 0, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 0, 0),
    )
    for (row in matrix) {
        println(row.contentToString())
    }
    val rows = 5
    val cols = 4

    val soop = matrix.makeWallsMatrix(rows, cols)
    for (row in soop) {
        println(row.contentToString())
    }

    val res = soop.makePolygonPointsLists(rows, cols)
    res.forEach { println(it) }

//    val kres = mutableListOf(res[1],res[0])
//
//    val insidePolygons2 = kres.separateInsidePolygons()
//    println(kres)
//    println(insidePolygons2)

    val insidePolygons = res.separateInsidePolygons()
    println(res)
    println(insidePolygons)
}