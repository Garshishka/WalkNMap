package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.LinearRing

data class MapPolygon(
    val points: MutableSet<MapPoint> = mutableSetOf()
) {
    override fun toString(): String {
        return points.toString()
    }
}

fun MapPolygon.mergePolygons(otherPolygon: MapPolygon, sharedPoints: Set<MapPoint>) {
    //we add other points from the second polygon
    this.points += otherPolygon.points
    //and we remove same points
    this.points -= sharedPoints
}

fun MapPolygon.sortPointsIntoDrawablePolygon(): MapPolygon {
    val sortedSet = mutableSetOf<MapPoint>()
    var lastInSorted = this.points.first()// this.points.elementAt(0)
    sortedSet.add(lastInSorted)
    //we add the first point from the polygon into new set
    this.points.remove(lastInSorted)
    while (this.points.isNotEmpty()) {
        //when we have a point in set that have same lat or lon - we put it in a new set and delete from old
        this.points.firstOrNull { lastInSorted.lat == it.lat || lastInSorted.lon == it.lon }
            ?.let { lastInSorted = it } ?: println("ALLO WTF ${this.points} and $sortedSet")
        sortedSet.add(lastInSorted)
        this.points.remove(lastInSorted)
        //and we do it until we spend all our points
    }
    return MapPolygon(sortedSet)
}

fun MapPolygon.makeLinearRing() : LinearRing {
    val list = this.points.map { it.toYandexPoint() }.toMutableList()
    list.add(this.points.first().toYandexPoint())
    return LinearRing(list)
}