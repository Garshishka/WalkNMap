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
    var lastInSorted = this.points.first()
    var lonMovement = true //first we move horizontal
    sortedSet.add(lastInSorted)
    //we add the first point from the polygon into new set
    this.points.remove(lastInSorted)
    //and remove it from unsorted list
    while (this.points.isNotEmpty()) {
        //we find the points in usorted polygon that lie oh the same axis as our movement now
        val pointsOnTheSameAxis =
            this.points.filter {
                if (lonMovement) {
                    it.lat == lastInSorted.lat
                } else {
                    it.lon == lastInSorted.lon
                }
            }
        if (pointsOnTheSameAxis.size == 1) {
            //if we get only one point - that's it
            lastInSorted = pointsOnTheSameAxis.first()
        } else {
            //if he have some points - we sort them by distance to the previous point
            pointsOnTheSameAxis.sortedBy { it.distanceToOtherPoint(lastInSorted) }
            //and we find closest point not behind some other already sorted point
                .first {
                    it.checkIfPointNotBehindAlreadyPlacedPoint(
                        sortedSet,
                        lastInSorted,
                        lonMovement
                    )
                }
                .let { lastInSorted = it }
        }
        //we add found point to sorted list, remove it from the usorted list and change movement axis
        sortedSet.add(lastInSorted)
        this.points.remove(lastInSorted)
        lonMovement = !lonMovement
        //and we do it until we spend all our points
    }
    return MapPolygon(sortedSet)
}

fun MapPolygon.makeLinearRing(): LinearRing {
    val list = this.points.map { it.toYandexPoint() }.toMutableList()
    list.add(this.points.first().toYandexPoint())
    return LinearRing(list)
}