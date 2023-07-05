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

    val res = soop.makePolygonPointsLists(rows,cols)
    res.forEach { println(it) }

    println(res[1].isInsideOtherPolygon(res[0]))
//
//res[1].forEach {
//    println(it.zepa(res[0]).toString()
//            + " $it")
//}
    val zhores : MutableList<MatrixLine> = mutableListOf()

    var oldKefa = res[1][0].zepa(res[0])
    println(oldKefa)
    for(i in 1..res[1].size-1){
        val kefa = res[1][i].zepa(res[0])
        println(kefa)
        if(oldKefa!=kefa){
            zhores.add(res[1][i].lineWith(res[1][i-1]))
        }
        oldKefa = kefa
    }
    println(zhores)

    zhores.forEach {
        for(i in 1..res[0].size-1){
            if(it.horizontal){
                if(res[0][i].lon==res[0][i-1].lon){
                    // if(res[0][i].lat)
                }
            }else{

            }
        }
    }





}


fun MatrixPoint.zepa(other: List<MatrixPoint>): PolygonState {
    var isInside = false
    var j = other.size - 1
    for (i in other.indices) {
        if ((other[i].lon > this.lon) != (other[j].lon > this.lon)) {
            if (this.lat < ((other[j].lat - other[i].lat) * (this.lat - other[i].lon) / (other[j].lon - other[i].lon) + other[i].lat)) {
                isInside = !isInside
                //println("this ${other[i]} - ${other[j]}")
            }
        }
        j = i
    }
    return if(isInside) PolygonState.INSIDE else PolygonState.OUTSIDE
}