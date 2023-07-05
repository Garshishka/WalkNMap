package ru.garshishka.walknmap.data

data class MatrixPoint(
    val lat : Int,
    val lon : Int,
)

data class MatrixLine(
    val horizontal : Boolean,
    val sameCoord : Int,
    val diffCoordMax : Int,
    val diffCoordMin : Int,
)

fun MatrixPoint.lineWith(other : MatrixPoint) : MatrixLine{
    val horizontal =  (this.lat == other.lat)

    return MatrixLine(
        horizontal,
        if(horizontal) this.lat else this.lon,
        if(horizontal) maxOf(this.lon, this.lat) else maxOf(this.lat, this.lon),
        if(horizontal) minOf(this.lon, this.lat) else minOf(this.lat, this.lon)
    )
}