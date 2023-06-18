package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PolygonMapObject
import ru.garshishka.walknmap.LAT_ADJUSTMENT
import ru.garshishka.walknmap.LON_ADJUSTMENT

fun PolygonMapObject.getCentralPoint(): MapPoint =
    this.geometry.outerRing.points[0].let {
        return MapPoint(it.latitude + LAT_ADJUSTMENT, it.longitude + LON_ADJUSTMENT)
    }


fun PolygonMapObject.checkCentralPoint(centralPoint: Point): Boolean =
    this.geometry.outerRing.points[0].latitude - LAT_ADJUSTMENT == centralPoint.latitude
            && this.geometry.outerRing.points[0].longitude - LON_ADJUSTMENT == centralPoint.longitude
