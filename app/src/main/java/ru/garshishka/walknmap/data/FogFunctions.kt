package ru.garshishka.walknmap.data

import com.yandex.mapkit.map.MapObjectCollection
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

fun List<MapPoint>.makePointMatrix(minPoint: MapPoint,rows: Int, cols: Int): Array<IntArray> {
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

fun Array<IntArray>.makeWallsMatrix(rows: Int, cols: Int): Array<IntArray> =
    Array(rows + 2) { r ->
        IntArray(cols + 2) { c ->
            if(r==rows+1 || c == cols+1){
                0
            }else {
                if (this[r][c] != this[r][c + 1]) {2} else {0} +
                        if (this[r][c] != this[r + 1][c]) {1} else {0}
            }
        }
    }

fun Array<IntArray>.makePolygonPointsLists(rows: Int, cols: Int): MutableList<MutableList<Pair<Int,Int>>>{
    val result = mutableListOf<MutableList<Pair<Int, Int>>>()

    for (r in 0..rows+1) {
        for (c in 0..cols+1) {
            if ((this[r][c] and 1) == 1) {
                var i = r + 1
                var j = c
                val cycle = mutableListOf((i to j))
                while (true) {
                    if (i < rows+1 && (this[i][j - 1] and 2) == 2) {
                        this[i][j - 1] -= 2
                        i++
                    } else if (i > 0 && (this[i - 1][j - 1] and 2) == 2) {
                        this[i - 1][j - 1] -= 2
                        i--
                    } else if (j < cols+1 && (this[i - 1][j] and 1 == 1)) {
                        this[i - 1][j] -= 1
                        j++
                    } else if (j > 0 && (this[i - 1][j-1] and 1 == 1)) {
                        this[i - 1][j-1] -= 1
                        j--
                    } else{
                        break
                    }
                    cycle.add(i to j)
                }
                result.add(cycle)
            }
        }
    }

    return result
}