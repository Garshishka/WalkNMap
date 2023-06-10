package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.MapScreenCoordinates
import ru.garshishka.walknmap.data.PointRepository
import java.time.OffsetDateTime

private val empty = MapPoint(0.0, 0.0, OffsetDateTime.now())
private val emptyPoints = mutableListOf<MapPoint>()

class MainViewModel(private val repository: PointRepository) : ViewModel() {
    val pointList = MutableLiveData(emptyPoints)//repository.getAll()//
    var oldPointList: List<MapPoint> = emptyList()
    val edited = MutableLiveData(empty)

    fun getPointsOnScreen(area: MapScreenCoordinates) {
        pointList.value?.let { oldPointList = it }
        pointList.value =
            repository.getPointsInArea(area.minLat, area.maxLat, area.minLon, area.maxLon).toMutableList()
    }

    fun empty() {
        edited.value = empty
    }

    fun save(point: MapPoint) = viewModelScope.launch {
        edited.value?.let {
            edited.value = point.copy(point.lat, point.lon)
        }
        edited.value?.let {
            repository.save(it)
            pointList.value!!.add(point)
        }
        empty()
    }

    fun delete(point: Point) {
        repository.delete(point.latitude, point.longitude)
    }

    fun getPoint(point: Point): Point? = repository.getPoint(point)
}