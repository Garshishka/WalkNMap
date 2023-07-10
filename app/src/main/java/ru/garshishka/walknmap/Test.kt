package ru.garshishka.walknmap

import ru.garshishka.walknmap.data.*

fun test() {


    val beepka = arrayOf(
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 1, 1, 0),
        intArrayOf(0, 1, 0, 1, 0),
        intArrayOf(0, 0, 1, 1, 0),
        intArrayOf(0, 0, 0, 0, 0),
    )
    for (row in beepka) {
        println(row.contentToString())
    }
    val rows = 3
    val cols = 3

    val soop = beepka.makeWallsMatrix(rows, cols)
    for (row in soop) {
        println(row.contentToString())
    }

    val res = soop.makePolygonPointsLists(rows, cols)
    res.forEach { println(it) }

    val outsideP = res[0]
    val insideP = res[1]

    println(insideP.isInsideOtherPolygon(outsideP))

    val interlockedLines: MutableList<MatrixLine> = mutableListOf()
    var oldStatus = insideP[0].isInsideOtherPolygon(outsideP)
    val insidePoints = mutableListOf<MatrixPoint>()
    val outsidePoints = mutableListOf<MatrixPoint>()
    println(oldStatus)
    for (i in 1 until insideP.size) {
        val status = insideP[i].isInsideOtherPolygon(outsideP)
        println(status)
        if(status == PolygonState.INSIDE){
            insidePoints.add(insideP[i])
        } else{
            outsidePoints.add(insideP[i])
        }
        if (oldStatus != status) {
            interlockedLines.add(insideP[i].lineWith(insideP[i - 1]))
        }
        oldStatus = status
    }
    println(insidePoints)
    println(interlockedLines)
    if(interlockedLines.size>2){
        throw Exception("Too much interlocking lines")
    }

    val intersectingPoints = mutableSetOf<MatrixPoint>()

    interlockedLines.forEach {intersectingPoints.add(it.findIntersectingPoint(outsideP)) }
    println(intersectingPoints)

    insidePoints.add(0,intersectingPoints.first())
    insidePoints.add(insidePoints.size,intersectingPoints.last())
    println(insidePoints)

    outsidePoints.add(0,intersectingPoints.first())
    outsidePoints.add(outsidePoints.size,intersectingPoints.last())
    println(outsidePoints)
}