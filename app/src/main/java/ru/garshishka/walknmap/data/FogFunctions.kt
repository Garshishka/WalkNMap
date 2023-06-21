package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import ru.garshishka.walknmap.LAT_ADJUSTMENT
import ru.garshishka.walknmap.LAT_ROUNDER
import ru.garshishka.walknmap.LON_ADJUSTMENT
import ru.garshishka.walknmap.LON_ROUNDER
import ru.garshishka.walknmap.viewmodel.MainViewModel
import kotlin.math.round

fun List<MapPoint>.addVerticalLinesOfFog(
    mapObjectCollection: MapObjectCollection,
    viewModel: MainViewModel
) {
    var lastPoint = MapPoint(0.0, 0.0)
    var firstPointInVertical = MapPoint(0.0, 0.0)
    this.forEach { point ->
        if (lastPoint == MapPoint(0.0, 0.0)) {
            lastPoint = point //for the first element
            firstPointInVertical = point
        } else {
            if (lastPoint.lon == point.lon) { //if the same column
                if (lastPoint.lat != point.lat - (LAT_ADJUSTMENT * 2)) { //if next point skipped some
                    viewModel.addVerticalLine(
                        mapObjectCollection,
                        lastPoint.toYandexPoint(),
                        firstPointInVertical.toYandexPoint()
                    )
                    firstPointInVertical = point
                }
                lastPoint = point //going to next
            } else { //if new column
                viewModel.addVerticalLine(
                    mapObjectCollection,
                    lastPoint.toYandexPoint(),
                    firstPointInVertical.toYandexPoint()
                )
                firstPointInVertical = point
                lastPoint = point //going to next
            }
        }
    }
}

fun List<MapPoint>.makeInsidePolygonList(): List<LinearRing> {
    val list = mutableListOf<LinearRing>()
    val latNewRounder = 2 * LAT_ROUNDER
    val lonNewRounder = 2 * LON_ROUNDER
    this.forEach {
        val maxLat = round((it.lat + LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
        val minLat = round((it.lat - LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
        val maxLon = round((it.lon + LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
        val minLon = round((it.lon - LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
        list.add(
            LinearRing(
                listOf(
                    Point(minLat, minLon),
                    Point(minLat, maxLon),
                    Point(maxLat, maxLon),
                    Point(maxLat, minLon),
                    Point(minLat, minLon),
                )
            )
        )
    }
    return list
}

fun MutableList<MapPolygon>.mergePolygonsInList() {
    val iterator = this.listIterator()

    while (iterator.hasNext()) {
        val polygon = iterator.next()
        var distinct = false
        //distinct help us find not connected squares
        while (!distinct) {
            distinct = true
            this.filterNot { it == polygon }.forEach { otherPolygon ->
                val samePoints = polygon.points.intersect(otherPolygon.points)
                //we get the same points between two polygons and delete them
                if (samePoints.checkIfFullConnection(polygon,otherPolygon)) {
                    polygon.mergePolygons(otherPolygon, samePoints)
                    iterator.remove()
                    iterator.next()
                    iterator.set(polygon)
                    //add what points left of other polygon delete other polygon
                    distinct = false
                }
            }
        }
    }
}

fun Set<MapPoint>.checkIfFullConnection(first: MapPolygon, second: MapPolygon): Boolean {
    when(this.size){
        0 -> return false
        1 -> {
            val connectedPoint = this.first()
            val firstLatAdjacent = first.points.first { it.lat == connectedPoint.lat && it.lon != connectedPoint.lon }
            val secondLatAdjacent = second.points.first { it.lat == connectedPoint.lat && it.lon != connectedPoint.lon }
            if (oneOrAnotherPointIsBetween(connectedPoint.lon, firstLatAdjacent.lon, secondLatAdjacent.lon)){
                return true
            }
            val firstLonAdjacent = first.points.first { it.lat != connectedPoint.lat && it.lon == connectedPoint.lon }
            val secondLonAdjacent = second.points.first { it.lat != connectedPoint.lat && it.lon == connectedPoint.lon }
            if (oneOrAnotherPointIsBetween(connectedPoint.lat, firstLonAdjacent.lat, secondLonAdjacent.lat)){
                return true
            }
            return false
        }
        else -> return true
    }
}

fun oneOrAnotherPointIsBetween(main : Double, first: Double, second: Double) : Boolean{
    return (main>first && first>second)||(main>second && second>first)
            ||(main<first && first<second)||(main<second && second<first)
}