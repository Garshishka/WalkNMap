package ru.garshishka.walknmap.data

data class MapPolygon(
    val points: MutableSet<MapPoint> = mutableSetOf()
) {
    override fun toString(): String {
        return points.toString()
    }
}

fun MapPolygon.mergePolygons(otherPolygon: MapPolygon, sharedPoints: Set<MapPoint>) : MapPolygon {
    val newPolygon = MapPolygon(this.points)
    //we add other points from the second polygon
    newPolygon.points += otherPolygon.points
    //and we remove same points
    newPolygon.points -= sharedPoints
    return newPolygon
}

fun MapPolygon.sortPointsIntoDrawablePolygon(): MapPolygon {
    val sortedSet = mutableSetOf<MapPoint>()
    var lastInSorted = this.points.first()// this.points.elementAt(0)
    sortedSet.add(lastInSorted)
    //we add the first point from the polygon into new set
    this.points.remove(lastInSorted)
    while (this.points.isNotEmpty()) {
        //when we have a point in set that have same lat or lon - we put it in a new set and delete from old
        this.points.first { lastInSorted.lat == it.lat || lastInSorted.lon == it.lon }
            .let { lastInSorted = it }
        sortedSet.add(lastInSorted)
        this.points.remove(lastInSorted)
        //and we do it until we spend all our points
    }
    return MapPolygon(sortedSet)
}
