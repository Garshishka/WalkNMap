package ru.garshishka.walknmap.data

fun testTestTesr() {
    //TODO DELETE

    val polygon1: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 1.0),
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 0.0),
        )
    )
    val polygon2: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(0.0, 1.0),
            MapPoint(0.0, 2.0),
            MapPoint(1.0, 2.0),
            MapPoint(1.0, 1.0),
        )
    )
    val polygon3: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 2.0),
            MapPoint(2.0, 2.0),
            MapPoint(2.0, 1.0),
        )
    )
    val polygon4: MapPolygon = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, 2.0),
            MapPoint(1.0, 3.0),
            MapPoint(2.0, 3.0),
            MapPoint(2.0, 2.0),
        )
    )
    val polygon5 = MapPolygon(
        mutableSetOf(
            MapPoint(2.0, 2.0),
            MapPoint(3.0, 2.0),
            MapPoint(3.0, 3.0),
            MapPoint(2.0, 3.0),
        )
    )
    val polygon6 = MapPolygon(
        mutableSetOf(
            MapPoint(2.0, 1.0),
            MapPoint(3.0, 1.0),
            MapPoint(3.0, 2.0),
            MapPoint(2.0, 2.0),
        )
    )
    val polygon7 = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, -2.0),
            MapPoint(1.0, -1.0),
            MapPoint(2.0, -1.0),
            MapPoint(2.0, -2.0),
        )
    )
    val polygon8 = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, -1.0),
            MapPoint(1.0, 0.0),
            MapPoint(2.0, 0.0),
            MapPoint(2.0, -1.0),
        )
    )
    val polygon9 = MapPolygon(
        mutableSetOf(
            MapPoint(2.0, -1.0),
            MapPoint(2.0, 0.0),
            MapPoint(3.0, 0.0),
            MapPoint(3.0, -1.0),
        )
    )
    val polygon10 = MapPolygon(
        mutableSetOf(
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 2.0),
            MapPoint(2.0, 2.0),
            MapPoint(2.0, 1.0),
        )
    )

    val polygonList = mutableListOf(
        polygon1,
        polygon2,
        polygon3,
        polygon4,
//        polygon5,
//        polygon6,
//        polygon7,
//        polygon8,
//        polygon9,
      //  polygon10
    )

    polygonList.mergePolygonsInList()

    println(polygonList)
    val fek = polygonList.map { it.sortPointsIntoDrawablePolygon() }
    println(fek)
}