package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.AreaCoordinates
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.PointRepository
import ru.garshishka.walknmap.data.toYandexPoint

private val emptyPoints = mutableListOf<MapPoint>()

class MainViewModel(private val repository: PointRepository) : ViewModel() {
    val pointList = MutableLiveData(emptyPoints)
    var oldPointList: List<MapPoint> = emptyList()

    fun getPointsOnScreen(area: AreaCoordinates) {
        pointList.value?.let { oldPointList = it }
        pointList.value =
            repository.getPointsInArea(area.minLat, area.maxLat, area.minLon, area.maxLon)
                .toMutableList()
    }

    fun save(point: MapPoint) = viewModelScope.launch {
        repository.save(point)
        pointList.value!!.add(point)
    }


    fun delete(point: Point) {
        repository.delete(point.latitude, point.longitude)
    }

    fun getPoint(point: Point): Point? = repository.getPoint(point)

    fun deletePointsOnNewSquareSize() {//Special function when rounding numbers change
        repository.getAll().forEach {
            if ((it.lat * 1000) % 1.0 != 0.0 || (it.lon * 1000) % 2.0 != 0.0) {
                delete(it.toYandexPoint())
            }
        }
        println(repository.getAll().size)
    }
}