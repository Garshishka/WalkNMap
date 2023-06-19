package ru.garshishka.walknmap.data

import com.yandex.mapkit.map.MapObjectCollection
import ru.garshishka.walknmap.LAT_ADJUSTMENT
import ru.garshishka.walknmap.viewmodel.MainViewModel

fun List<MapPoint>.addVerticalLinesOfFog(
    mapObjectCollection: MapObjectCollection,
    viewModel: MainViewModel
) {
    var lastPoint = MapPoint(0.0, 0.0)
    var firstPointInVertical = MapPoint(0.0,0.0)
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
            } else{ //if new column
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