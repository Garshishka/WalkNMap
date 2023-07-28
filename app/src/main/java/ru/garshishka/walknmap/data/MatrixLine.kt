package ru.garshishka.walknmap.data

data class MatrixLine(
    val horizontal : Boolean,
    val sameCoord : Int,
    val diffCoordMax : Int,
    val diffCoordMin : Int,
){
    override fun toString(): String {
        if(horizontal){
            return "$sameCoord|$diffCoordMin..$diffCoordMax"
        } else{
            return "$diffCoordMin..$diffCoordMax|$sameCoord"
        }
    }
}

fun MatrixLine.findIntersectingPoint(otherPolygon: List<MatrixPoint>) : MatrixPoint{
    for (i in 1 until otherPolygon.size) {
        if (this.horizontal) {
            if (otherPolygon[i].lon == otherPolygon[i - 1].lon &&
                otherPolygon[i].lon < this.diffCoordMax &&
                otherPolygon[i].lon > this.diffCoordMin &&
                this.sameCoord < maxOf(otherPolygon[i].lat, otherPolygon[i - 1].lat) &&
                this.sameCoord > minOf(otherPolygon[i].lat, otherPolygon[i - 1].lat)) {
                return MatrixPoint(this.sameCoord, otherPolygon[i].lon)
            }
        } else {
            if (otherPolygon[i].lat == otherPolygon[i - 1].lat &&
                otherPolygon[i].lat < this.diffCoordMax &&
                otherPolygon[i].lat > this.diffCoordMin &&
                this.sameCoord < maxOf(otherPolygon[i].lon, otherPolygon[i - 1].lon) &&
                this.sameCoord > minOf(otherPolygon[i].lon, otherPolygon[i - 1].lon)) {
                return MatrixPoint(otherPolygon[i].lat, this.sameCoord)
            }
        }
    }
    println(this)
    println(otherPolygon)
    throw Exception("No intersecting point found!")
}
