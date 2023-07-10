package ru.garshishka.walknmap.data

data class MatrixPoint(
    val lat : Int,
    val lon : Int,
){
    override fun toString(): String {
        return "$lat|$lon"
    }
}

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


fun MatrixPoint.isInsideOtherPolygon(other: List<MatrixPoint>): PolygonState {
    var isInside = false
    var j = other.size - 1
    for (i in other.indices) {
        if ((other[i].lon > this.lon) != (other[j].lon > this.lon)) {
            if (this.lat < ((other[j].lat - other[i].lat) * (this.lat - other[i].lon) / (other[j].lon - other[i].lon) + other[i].lat)) {
                isInside = !isInside
            }
        }
        j = i
    }
    return if (isInside) PolygonState.INSIDE else PolygonState.OUTSIDE
}

fun MatrixPoint.lineWith(other : MatrixPoint) : MatrixLine{
    val horizontal =  (this.lat == other.lat)

    return MatrixLine(
        horizontal,
        if(horizontal) this.lat else this.lon,
        if(horizontal) maxOf(this.lon, other.lon) else maxOf(this.lat, other.lat),
        if(horizontal) minOf(this.lon, other.lon) else minOf(this.lat, other.lat)
    )
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
    throw Exception("No intersecting point found!")
}