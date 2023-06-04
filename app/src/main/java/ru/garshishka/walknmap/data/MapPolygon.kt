package ru.garshishka.walknmap.data

data class MapPolygon(
    val points: MutableSet<MapPoint> = mutableSetOf()
){
    override fun toString(): String {
        return points.toString()
    }
}

fun MapPolygon.mergePolygons(otherPolygon: MapPolygon, sharedPoints : Set<MapPoint>) {
    //we add other points from the second polygon
    this.points += otherPolygon.points
    //and we remove same points
    this.points -= sharedPoints
}
