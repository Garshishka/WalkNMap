package ru.garshishka.walknmap.data

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

fun mergeTesting() {
    val polygon1: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(0.0, 0.0),
            MapPoint(1.0, 0.0),
            MapPoint(1.0, 1.0),
            MapPoint(0.0, 1.0),
        )
    )
    println(polygon1)
    val polygon2: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(0.0, 1.0),
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 2.0),
            MapPoint(0.0, 2.0),
        )
    )
    val polygon3: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(0.0, 2.0),
            MapPoint(1.0, 2.0),
            MapPoint(1.0, 3.0),
            MapPoint(0.0, 3.0),
        )
    )
    val polygon4: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, 2.0),
            MapPoint(2.0, 2.0),
            MapPoint(2.0, 3.0),
            MapPoint(1.0, 3.0),
        )
    )
    val polygon5: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(2.0, 2.0),
            MapPoint(3.0, 2.0),
            MapPoint(3.0, 3.0),
            MapPoint(2.0, 3.0),
        )
    )
    val polygon6: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(2.0, 1.0),
            MapPoint(3.0, 1.0),
            MapPoint(3.0, 2.0),
            MapPoint(2.0, 2.0),
        )
    )

    val polygonList = mutableListOf(polygon1, polygon2, polygon3, polygon4, polygon5, polygon6)
    println(polygonList)

    polygonList.forEach { polygon ->
        var distinct = false
        while (!distinct) {
            distinct = true
            polygonList.filterNot { it == polygon }.forEach { otherPolygon ->
                val samePoints = polygon.points.intersect(otherPolygon.points)
                if (!samePoints.isEmpty()) {
                    polygon.mergePolygons(otherPolygon, samePoints)
                    polygonList -= otherPolygon
                    distinct = false
                    print("got $polygon")
                }
            }
        }
    }

    println(polygonList)
}
