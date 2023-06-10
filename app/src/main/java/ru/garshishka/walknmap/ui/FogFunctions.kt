package ru.garshishka.walknmap.ui

import android.graphics.Color
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolygonMapObject
import ru.garshishka.walknmap.*
import ru.garshishka.walknmap.data.AreaCoordinates

fun makeBoundingPolygon(
    insideArea: AreaCoordinates,
    mapObjectCollection: MapObjectCollection
): PolygonMapObject {
    return mapObjectCollection.addPolygon(
        Polygon(
            LinearRing(
                listOf(
                    Point(TOP_LAT, LEFT_LON),
                    Point(BOTTOM_LAT, LEFT_LON),
                    Point(BOTTOM_LAT, RIGHT_LON),
                    Point(TOP_LAT, RIGHT_LON)
                )
            ),
            listOf(
                LinearRing(
                    listOf(
                        Point(
                            insideArea.maxLat + LAT_ADJUSTMENT,
                            insideArea.minLon - LON_ADJUSTMENT
                        ),
                        Point(
                            insideArea.minLat - LAT_ADJUSTMENT,
                            insideArea.minLon - LON_ADJUSTMENT
                        ),
                        Point(
                            insideArea.minLat - LAT_ADJUSTMENT,
                            insideArea.maxLon + LON_ADJUSTMENT
                        ),
                        Point(
                            insideArea.maxLat + LAT_ADJUSTMENT,
                            insideArea.maxLon + LON_ADJUSTMENT
                        ),
                    )
                )
            )
        )
    ).also {
        it.fillColor = FOG_COLOR
        it.strokeColor = Color.TRANSPARENT
    }
}