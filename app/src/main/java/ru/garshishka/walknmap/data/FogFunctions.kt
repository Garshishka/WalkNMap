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
    val latNewRounder = 2* LAT_ROUNDER
    val lonNewRounder = 2* LON_ROUNDER
    this.forEach {
        val maxLat = round((it.lat + LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
        val minLat = round((it.lat - LAT_ADJUSTMENT) * latNewRounder) / latNewRounder
        val maxLon = round((it.lon + LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
        val minLon = round((it.lon - LON_ADJUSTMENT) * lonNewRounder) / lonNewRounder
        list.add(
            LinearRing(
                listOf(
                    Point(minLat,minLon),
                    Point(minLat,maxLon),
                    Point(maxLat,maxLon),
                    Point(maxLat,minLon),
                    Point(minLat,minLon),
                )
            )
        )
    }
    return list
}