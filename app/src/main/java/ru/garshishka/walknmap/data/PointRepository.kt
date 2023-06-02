package ru.garshishka.walknmap.data

import androidx.lifecycle.map
import com.yandex.mapkit.geometry.Point

class PointRepository(
    private val dao: GridPointDao
) {
    fun getAll() = dao.getAll().map { list ->
        list.map {
            GridPoint(Point(it.lat, it.lon), it.enabled)
        }
    }

    suspend fun save(place: GridPoint) {
        dao.save(GridEntity.fromDto(place))
    }

    fun delete(lat: Double, lon: Double){
        dao.deleteById(lat, lon)
    }

    fun getPoint(point: Point): GridPoint? {
        val entity = dao.findPoint(point.latitude, point.longitude)
        return entity?.let{ GridPoint(Point(it.lat,it.lon), it.enabled)}
    }
}
