package ru.garshishka.walknmap.data

//Class to represent our points on an integer matrix for polygon assembling functions
data class MatrixPoint(
    val lat : Int,
    val lon : Int,
){
    override fun toString(): String {
        return "$lat|$lon"
    }
}

fun MatrixPoint.isInsideOtherPolygon(other: List<MatrixPoint>): PolygonState {
    var isInside = false
    var j = other.size - 1
    //This algorithm is based on quick version of raytracing algorithm
    //Points sends a "ray" and "count" every time this ray crosses other polygon walls
    //If the number of crosses is odd - point is inside. Even - it is not
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
