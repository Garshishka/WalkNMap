package ru.garshishka.walknmap.data

import androidx.lifecycle.map
import com.yandex.mapkit.geometry.Point

class PointRepository(
    private val dao: GridPointDao
) {
    fun getAll() = dao.getAll().map { list ->
        list.map {
            MapPoint(it.lat, it.lon)
        }
    }

    fun getPointsInArea(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) =
        dao.findPointsInArea(minLat, maxLat, minLon, maxLon).map {
            MapPoint(it.lat, it.lon)
        }


    suspend fun save(place: MapPoint) {
        dao.save(GridEntity.fromDto(place))
    }

    fun delete(lat: Double, lon: Double) {
        dao.deleteById(lat, lon)
    }

    fun getPoint(point: Point): Point? {
        val entity = dao.findPoint(point.latitude, point.longitude)
        return entity?.let { Point(it.lat, it.lon) }
    }
}
