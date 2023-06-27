package ru.garshishka.walknmap.data

import com.yandex.mapkit.map.MapObjectCollection
import ru.garshishka.walknmap.DOUBLE_LAT_ADJUSTMENT
import ru.garshishka.walknmap.DOUBLE_LON_ADJUSTMENT
import ru.garshishka.walknmap.LAT_ADJUSTMENT
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

fun List<MapPoint>.makePointMatrix() : Array<IntArray> {
    val minPoint = MapPoint(this.minBy { it.lat }.lat, this.minBy { it.lon }.lon)
    val maxPoint = MapPoint(this.maxBy { it.lat }.lat, this.maxBy { it.lon }.lon)

    val rows = ((maxPoint.lat - minPoint.lat) / DOUBLE_LAT_ADJUSTMENT).toInt() + 1
    val cols = ((maxPoint.lon - minPoint.lon) / DOUBLE_LON_ADJUSTMENT).toInt() + 1

    return Array(rows + 2) { r ->
        IntArray(cols + 2) { c ->
            if (r == 0 || c == 0 || r == rows + 1 || c == cols + 1) {
                0
            } else {
                if (this.contains(
                        MapPoint(
                            round((minPoint.lat + ((r - 1) * 0.001)) * 1000) / 1000,
                            round((minPoint.lon + ((c - 1) * 0.002)) * 500) / 500,
                        )
                    )
                ) {
                    1
                } else {
                    0
                }
            }
        }
    }
}