package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point
import ru.garshishka.walknmap.db.MapPointDao
import ru.garshishka.walknmap.db.MapPointEntity

class PointRepository(
    private val dao: MapPointDao
) {
    fun getAll() = dao.getAll().map { it.toDto() }

    suspend fun getPointsInArea(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) =
        dao.findPointsInArea(minLat, maxLat, minLon, maxLon).map {
            it.toDto()
        }


    suspend fun save(place: MapPoint) {
        dao.save(MapPointEntity.fromDto(place))
    }

    fun delete(lat: Double, lon: Double) {
        dao.deleteById(lat, lon)
    }

    fun getPoint(point: Point): Point? {
        val entity = dao.findPoint(point.latitude, point.longitude)
        return entity?.let { Point(it.lat, it.lon) }
    }
}
